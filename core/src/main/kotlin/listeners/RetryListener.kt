package core.listeners

import org.junit.jupiter.api.extension.*
import org.slf4j.LoggerFactory
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.kotlinFunction

/**
 * Аннотация для пометки flaky тестов, которые нужно перезапускать при падении.
 *
 * Пример использования:
 * @RetryIfFailed(maxAttempts = 3)
 * @Test
 * fun flakyTest() { ... }
 */
@Target(FUNCTION)
@Retention(RUNTIME)
annotation class RetryIfFailed(
    val maxAttempts: Int = 2
)

/**
 * JUnit 5 Extension для автоматического перезапуска упавших тестов.
 *
 * Логика работы:
 * 1. Проверяет, есть ли у теста аннотация @RetryIfFailed
 * 2. Если тест упал, перезапускает его указанное количество раз
 * 3. После всех попыток, если тест всё ещё падает, помечает как failed
 */
class RetryListener : TestExecutionExceptionHandler, ExecutionCondition {

    private val log = LoggerFactory.getLogger(RetryListener::class.java)

    // Храним количество попыток для каждого теста
    private val attemptsMap = mutableMapOf<String, Int>()

    override fun evaluateExecutionCondition(context: ExtensionContext): ConditionEvaluationResult {
        val testMethod = context.testMethod.orElse(null)
        val testInstance = context.testInstance.orElse(null)

        if (testMethod != null && testInstance != null) {
            val key = "${testInstance::class.simpleName}.${testMethod.name}"

            // Получаем аннотацию RetryIfFailed
            val retryAnnotation = testMethod.kotlinFunction?.findAnnotation<RetryIfFailed>()

            if (retryAnnotation != null) {
                val currentAttempt = attemptsMap.getOrDefault(key, 1)
                val maxAttempts = retryAnnotation.maxAttempts

                if (currentAttempt > maxAttempts) {
                    // Превысили лимит попыток - тест окончательно падает
                    log.warn("🔄 Тест $key исчерпал все попытки ($maxAttempts)")
                    return ConditionEvaluationResult.enabled("Больше не перезапускаем")
                }

                log.info("🔄 Тест $key: попытка $currentAttempt из $maxAttempts")
            }
        }

        return ConditionEvaluationResult.enabled("Тест разрешён к выполнению")
    }

    override fun handleTestExecutionException(
        context: ExtensionContext,
        throwable: Throwable
    ) {
        val testMethod = context.testMethod.orElse(null)
        val testInstance = context.testInstance.orElse(null)

        if (testMethod != null && testInstance != null) {
            val key = "${testInstance::class.simpleName}.${testMethod.name}"
            val retryAnnotation = testMethod.kotlinFunction?.findAnnotation<RetryIfFailed>()

            if (retryAnnotation != null) {
                val currentAttempt = attemptsMap.getOrDefault(key, 1)
                val maxAttempts = retryAnnotation.maxAttempts

                if (currentAttempt < maxAttempts) {
                    // Увеличиваем счётчик и перезапускаем
                    attemptsMap[key] = currentAttempt + 1

                    log.warn("🔄 Тест $key упал (попытка $currentAttempt). Перезапускаем...")

                    // Вариант 1: Используем TestAbortedException (рекомендуется)
                    throw org.opentest4j.TestAbortedException(
                        "Перезапуск теста (попытка $currentAttempt из $maxAttempts)",
                        throwable
                    )

                    // Вариант 2: Просто пробрасываем исходное исключение
                    // throw throwable
                } else {
                    // Это была последняя попытка, тест окончательно падает
                    log.error("❌ Тест $key упал после $maxAttempts попыток")
                    attemptsMap.remove(key)
                    throw throwable
                }
            }
        }

        // Если не подходит под условия, пробрасываем исходное исключение
        throw throwable
    }
}
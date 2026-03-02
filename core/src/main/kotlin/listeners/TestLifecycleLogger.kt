package core.listeners

import org.junit.jupiter.api.extension.*
import org.slf4j.LoggerFactory

/**
 * JUnit 5 Extension для логирования жизненного цикла тестов.
 *
 * Логирует все важные события:
 * - Начало выполнения теста
 * - Успешное завершение
 * - Падение с ошибкой
 * - Пропуск теста
 */
class TestLifecycleLogger :
    BeforeTestExecutionCallback,
    AfterTestExecutionCallback,
    TestExecutionExceptionHandler {

    private val log = LoggerFactory.getLogger(TestLifecycleLogger::class.java)

    override fun beforeTestExecution(context: ExtensionContext) {
        val testMethod = context.testMethod.map { it.name }.orElse("unknown")
        val testClass = context.testClass.map { it.simpleName }.orElse("unknown")

        log.info("▶️ [${testClass}.$testMethod] НАЧАЛО ВЫПОЛНЕНИЯ")

        // Сохраняем время старта в store
        val store = context.getStore(ExtensionContext.Namespace.create(context.requiredTestMethod))
        store.put("startTime", System.currentTimeMillis())
    }

    override fun afterTestExecution(context: ExtensionContext) {
        val testMethod = context.testMethod.map { it.name }.orElse("unknown")
        val testClass = context.testClass.map { it.simpleName }.orElse("unknown")

        val store = context.getStore(ExtensionContext.Namespace.create(context.requiredTestMethod))
        val startTime = store.get("startTime", Long::class.java)
        val duration = if (startTime != null) System.currentTimeMillis() - startTime else 0

        val status = if (context.executionException.isPresent) "❌ ПАДЕНИЕ" else "✅ УСПЕХ"

        log.info("${status} [${testClass}.$testMethod] Длительность: ${duration}ms")
    }

    override fun handleTestExecutionException(
        context: ExtensionContext,
        throwable: Throwable
    ) {
        val testMethod = context.testMethod.map { it.name }.orElse("unknown")
        val testClass = context.testClass.map { it.simpleName }.orElse("unknown")

        log.error("💥 [${testClass}.$testMethod] Исключение: ${throwable.message}")

        // Пробрасываем исключение дальше, чтобы JUnit обработал его
        throw throwable
    }
}
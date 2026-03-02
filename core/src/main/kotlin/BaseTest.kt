package core

import config.TestConfig
import core.listeners.AllureAttachmentListener
import core.listeners.RetryListener
import core.listeners.TestLifecycleLogger
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant

/**
 * Базовый класс для ВСЕХ тестов в проекте (UI, API, нагрузочных).
 *
 * Содержит общую инфраструктуру:
 * - Логирование начала/окончания тестов
 * - Allure интеграцию (скриншоты при падении)
 * - Retry механизм для flaky тестов
 * - Таймауты для тестов
 * - Общие вспомогательные методы
 */
@ExtendWith(
    TestLifecycleLogger::class,
    AllureAttachmentListener::class,
    RetryListener::class
)
abstract class BaseTest {

    companion object {
        val log: Logger = LoggerFactory.getLogger(BaseTest::class.java)

        @BeforeAll
        @JvmStatic
        fun globalSetUp() {
            log.info("=".repeat(80))
            log.info("🚀 НАЧАЛО ТЕСТОВОГО ЗАПУСКА")
            log.info("   Окружение: ${TestConfig.env}")
            log.info("   Время: ${Instant.now()}")
            log.info("=".repeat(80))
        }

        @AfterAll
        @JvmStatic
        fun globalTearDown() {
            log.info("=".repeat(80))
            log.info("✅ ЗАВЕРШЕНИЕ ТЕСТОВОГО ЗАПУСКА")
            log.info("   Время: ${Instant.now()}")
            log.info("=".repeat(80))
        }
    }

    /**
     * Логирование перед каждым тестом
     */
    @BeforeEach
    fun baseSetUp(testInfo: TestInfo) {
        log.info("▶️ Начинаем тест: ${testInfo.displayName}")
        log.debug("   Метод: ${testInfo.testMethod.orElse(null)}")
        log.debug("   Теги: ${testInfo.tags}")
    }

    /**
     * Логирование после каждого теста
     */
    @AfterEach
    fun baseTearDown(testInfo: TestInfo) {
        log.info("⏹️ Завершён тест: ${testInfo.displayName}")
    }

    /**
     * Универсальный метод для повторяющихся действий с таймаутом
     */
    protected fun <T> waitFor(
        timeoutSeconds: Long = 10,
        pollIntervalMillis: Long = 500,
        action: () -> T
    ): T {
        val startTime = Instant.now()
        val timeout = Duration.ofSeconds(timeoutSeconds)

        while (Duration.between(startTime, Instant.now()) < timeout) {
            try {
                val result = action()
                if (result != null && result != false) {
                    return result
                }
            } catch (e: Exception) {
                log.debug("Waiting for condition, attempt failed: ${e.message}")
            }
            Thread.sleep(pollIntervalMillis)
        }
        throw AssertionError("Timeout after ${timeoutSeconds}s waiting for condition")
    }

    /**
     * Получить случайную строку (для тестовых данных)
     */
    protected fun randomString(prefix: String = "test"): String {
        return "${prefix}_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }

    /**
     * Получить случайный email
     */
    protected fun randomEmail(): String {
        return "test_${System.currentTimeMillis()}@example.com"
    }
}
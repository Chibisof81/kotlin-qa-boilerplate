package base

// Импорты из core модуля (работают благодаря зависимости)
import config.BrowserConfig
import core.BaseTest
import org.junit.jupiter.api.*

/**
 * Базовый класс для UI-тестов.
 * Наследуется от BaseTest из модуля core.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseUiTest : BaseTest() {

    @BeforeAll
    fun setUpBrowser() {
        log.info("🌐 Настройка браузера для UI-тестов")
        BrowserConfig.setup()
    }

    @BeforeEach
    fun setUpUiTest(testInfo: TestInfo) {
        log.info("🖥️ UI тест: ${testInfo.displayName}")

        // Используем методы из BaseTest
        val testEmail = randomEmail()  // метод из BaseTest работает!
    }

    @AfterAll
    fun tearDownBrowser() {
        log.info("🧹 Очистка браузера после UI-тестов")
        com.codeborne.selenide.Selenide.clearBrowserCookies()
        com.codeborne.selenide.Selenide.clearBrowserLocalStorage()
        BrowserConfig.teardown()
    }
}
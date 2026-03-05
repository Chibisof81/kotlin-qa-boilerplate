package base

// Импорты из core модуля (работают благодаря зависимости)
import com.codeborne.selenide.Selenide
import core.BaseTest
import core.core.config.BrowserConfig
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
        Selenide.clearBrowserCookies()
        Selenide.clearBrowserLocalStorage()
        Selenide.closeWebDriver()
        BrowserConfig.teardown()
    }
}
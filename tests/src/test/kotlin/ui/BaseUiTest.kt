package ui

import core.core.config.BrowserConfig
import com.codeborne.selenide.Selenide
import core.BaseTest
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // ✅ Один экземпляр класса на все тесты
open class BaseUiTest: BaseTest() {

    @BeforeAll
    open fun setupAll() {
        // ✅ Контейнер стартует ОДИН РАЗ перед всеми тестами класса
        BrowserConfig.setup()
    }

    @BeforeEach
    open fun setupTest() {
        // ✅ Чистая сессия для каждого теста (новая вкладка)
        Selenide.open("/")
        Selenide.clearBrowserCookies()
        Selenide.clearBrowserLocalStorage()
    }

    @AfterEach
    open fun teardownTest() {
        // ✅ Закрываем вкладку, но НЕ контейнер
        Selenide.closeWebDriver()
    }

    @AfterAll
    open fun teardownAll() {
        // ✅ Контейнер останавливается ПОСЛЕ всех тестов класса
        BrowserConfig.teardown()
    }
}
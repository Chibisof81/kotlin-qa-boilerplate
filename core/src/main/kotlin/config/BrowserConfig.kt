package config

import com.codeborne.selenide.Configuration
import com.codeborne.selenide.logevents.SelenideLogger
import io.qameta.allure.selenide.AllureSelenide
import org.openqa.selenium.PageLoadStrategy
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.edge.EdgeOptions
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
import org.testcontainers.containers.BrowserWebDriverContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.time.Duration

/**
 * Конфигурация браузера с поддержкой трёх режимов:
 * - LOCAL    : локальный запуск (headless по умолчанию)
 * - SELENOID : запуск в Selenoid Grid (с VNC/видео)
 * - TESTCONTAINERS : запуск в Testcontainers (изолированный контейнер)
 *
 * Все настройки берутся из TestConfig (который читает properties и system properties).
 */
object BrowserConfig {

    enum class RunMode {
        LOCAL, SELENOID, TESTCONTAINERS
    }

    // Режим запуска из конфига
    private val mode: RunMode by lazy {
        when (TestConfig.Browser.mode.lowercase()) {
            "selenoid" -> RunMode.SELENOID
            "testcontainers" -> RunMode.TESTCONTAINERS
            else -> RunMode.LOCAL
        }
    }

    // Контейнер для режима TESTCONTAINERS
    private var webDriverContainer: BrowserWebDriverContainer<*>? = null

    /**
     * Основная настройка - вызывать перед всеми тестами
     */
    fun setup() {
        // 1. Базовые настройки Selenide из конфига
        Configuration.timeout = TestConfig.Browser.timeout
        Configuration.browserSize = TestConfig.Browser.size
        Configuration.headless = TestConfig.Browser.headless && mode == RunMode.LOCAL
        Configuration.baseUrl = TestConfig.uiBaseUrl
        Configuration.reportsFolder = "build/reports/tests"
        Configuration.screenshots = true
        Configuration.savePageSource = false

        // 2. Allure интеграция
        SelenideLogger.addListener(
            "AllureSelenide",
            AllureSelenide().screenshots(true).savePageSource(false)
        )

        // 3. Логирование (полезно для отладки)
        println("🌐 BrowserConfig: starting in $mode mode")
        println("   Browser: ${TestConfig.Browser.name} ${TestConfig.Browser.version}")
        println("   Timeout: ${TestConfig.Browser.timeout}ms")
        println("   Headless: ${Configuration.headless}")
        println("   Base URL: ${Configuration.baseUrl}")

        // 4. Переключение режимов
        when (mode) {
            RunMode.LOCAL -> setupLocal()
            RunMode.SELENOID -> setupSelenoid()
            RunMode.TESTCONTAINERS -> setupTestcontainers()
        }
    }

    private fun setupLocal() {
        Configuration.remote = null
        Configuration.browserCapabilities = createCapabilities()
    }

    private fun setupSelenoid() {
        Configuration.remote = TestConfig.Browser.selenoidUrl
        Configuration.browserVersion = TestConfig.Browser.version

        val capabilities = createCapabilities().apply {
            setCapability("selenoid:options", mapOf(
                "enableVNC" to TestConfig.Browser.selenoidEnableVnc,
                "enableVideo" to TestConfig.Browser.selenoidEnableVideo,
                "enableLog" to TestConfig.Browser.selenoidEnableLog,
                "name" to "Kotlin Test Framework"
            ))
        }
        Configuration.browserCapabilities = capabilities

        println("   Selenoid URL: ${Configuration.remote}")
    }

    private fun setupTestcontainers() {
        if (webDriverContainer == null) {
            webDriverContainer = BrowserWebDriverContainer().apply {
                withCapabilities(createCapabilities())
                // Опционально: открыть VNC для отладки (порт 7900)
                if (TestConfig.Browser.selenoidEnableVnc) {
                    addExposedPort(7900)
                }
                waitingFor(Wait.forHttp("/status").forPort(4444).forStatusCode(200))
                withStartupTimeout(Duration.ofSeconds(30))
                start()
            }
        }

        Configuration.remote = webDriverContainer!!.seleniumAddress.toString()
        Configuration.browserCapabilities = createCapabilities()

        // Полезные ссылки для отладки
        if (TestConfig.Browser.selenoidEnableVnc) {
            println("🔴 Testcontainers VNC: http://localhost:${webDriverContainer!!.getMappedPort(7900)}/?password=secret")
        }
        println("🔴 Selenium UI: http://localhost:${webDriverContainer!!.getMappedPort(4444)}/ui/#/sessions")
    }

    /**
     * Создаёт капабилити для выбранного браузера
     */
    private fun createCapabilities(): DesiredCapabilities {
        val browserName = TestConfig.Browser.name.lowercase()

        return when (browserName) {
            "chrome" -> createChromeCapabilities()
            "firefox" -> createFirefoxCapabilities()
            "edge" -> createEdgeCapabilities()
            else -> error("Unsupported browser: $browserName")
        }
    }

    private fun createChromeCapabilities(): DesiredCapabilities {
        val options = ChromeOptions().apply {
            addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--remote-allow-origins=*",
                "--disable-infobars",
                "--disable-notifications",
                "--disable-extensions",
                "use-fake-device-for-media-stream",
                "use-fake-ui-for-media-stream",
                "--disable-webrtc",
                "--disable-blink-features=AutomationControlled",
                "--disable-web-security"
            )

            // Если headless, добавляем соответствующие аргументы
            if (Configuration.headless) {
                addArguments("--headless=new")
            }
        }

        return DesiredCapabilities().apply {
            setCapability("browserName", "chrome")
            setCapability(CapabilityType.PAGE_LOAD_STRATEGY, PageLoadStrategy.NORMAL)
            setCapability(ChromeOptions.CAPABILITY, options)
        }
    }

    private fun createFirefoxCapabilities(): DesiredCapabilities {
        val options = FirefoxOptions().apply {
            addArguments("--width", TestConfig.Browser.size.split("x")[0])
            addArguments("--height", TestConfig.Browser.size.split("x")[1])

            if (Configuration.headless) {
                addArguments("--headless")
            }
        }

        return DesiredCapabilities().apply {
            setCapability("browserName", "firefox")
            setCapability(CapabilityType.PAGE_LOAD_STRATEGY, PageLoadStrategy.NORMAL)
            setCapability(FirefoxOptions.FIREFOX_OPTIONS, options)
        }
    }

    private fun createEdgeCapabilities(): DesiredCapabilities {
        val options = EdgeOptions().apply {
            addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--remote-allow-origins=*",
                "--disable-notifications"
            )

            if (Configuration.headless) {
                addArguments("--headless=new")
            }
        }

        return DesiredCapabilities().apply {
            setCapability("browserName", "MicrosoftEdge")
            setCapability(CapabilityType.PAGE_LOAD_STRATEGY, PageLoadStrategy.NORMAL)
            setCapability(EdgeOptions.CAPABILITY, options)
        }
    }

    /**
     * Завершение работы - вызывать после всех тестов
     */
    fun teardown() {
        println("🧹 BrowserConfig: cleaning up")
        try {
//            Selenide.closeWebDriver()
            val driver = com.codeborne.selenide.WebDriverRunner.getWebDriver()
            driver.quit()
        } catch (e: Exception) {
            println("⚠️ Error closing WebDriver: ${e.message}")
        }

        try {
            webDriverContainer?.stop()
            webDriverContainer = null
        } catch (e: Exception) {
            println("⚠️ Error stopping Testcontainers: ${e.message}")
        }
    }
}
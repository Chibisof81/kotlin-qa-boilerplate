package core.core.config
import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.logevents.SelenideLogger
import core.config.TestConfig
import io.qameta.allure.selenide.AllureSelenide
import org.openqa.selenium.PageLoadStrategy
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.edge.EdgeOptions
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
import org.slf4j.LoggerFactory
import org.testcontainers.containers.BrowserWebDriverContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import java.time.Duration

object BrowserConfig {
    enum class RunMode {
        LOCAL,
        SELENOID,
        TESTCONTAINERS
    }

    private val mode: RunMode by lazy {
        when (TestConfig.Browser.mode.lowercase()) {
            "selenoid" -> RunMode.SELENOID
            "testcontainers" -> RunMode.TESTCONTAINERS
            else -> RunMode.LOCAL
        }
    }

    private var webDriverContainer: BrowserWebDriverContainer<*>? = null
    private val tcLogger = LoggerFactory.getLogger("org.testcontainers")

    fun setup() {
        Configuration.timeout = TestConfig.Browser.timeout
        Configuration.browserSize = TestConfig.Browser.size
        Configuration.headless = TestConfig.Browser.headless && mode == RunMode.LOCAL
        Configuration.baseUrl = TestConfig.uiBaseUrl
        Configuration.reportsFolder = "build/reports/tests"
        Configuration.screenshots = true
        Configuration.savePageSource = false

        setupAllureLogger()

        println("🌐 BrowserConfig: starting in $mode mode")
        println("   Browser: ${TestConfig.Browser.name} ${TestConfig.Browser.version}")
        println("   Timeout: ${TestConfig.Browser.timeout}ms")
        println("   Headless: ${Configuration.headless}")
        println("   Base URL: ${Configuration.baseUrl}")

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
            setCapability(
                "selenoid:options", mapOf(
                    "enableVNC" to TestConfig.Browser.selenoidEnableVnc,
                    "enableVideo" to TestConfig.Browser.selenoidEnableVideo,
                    "enableLog" to TestConfig.Browser.selenoidEnableLog,
                    "name" to "Kotlin Test Framework"
                )
            )
        }
        Configuration.browserCapabilities = capabilities
        println("   Selenoid URL: ${Configuration.remote}")
    }

    private fun setupTestcontainers() {
        if (webDriverContainer == null) {
            println("🐳 Starting Testcontainers WebDriver...")
            try {
                // ✅ Правильный образ для Testcontainers
                val browser = TestConfig.Browser.name.lowercase()
                val version = TestConfig.Browser.version

                val imageName = when (browser) {
                    "chrome" -> "selenium/standalone-chrome:$version"
                    "firefox" -> "selenium/standalone-firefox:$version"
                    "edge" -> "selenium/standalone-edge:$version"
                    else -> "selenium/standalone-chrome:$version"
                }

                webDriverContainer = BrowserWebDriverContainer(
                    DockerImageName.parse(imageName)
                ).apply {
                    withCapabilities(createCapabilities())
                    withLogConsumer(Slf4jLogConsumer(tcLogger))

                    if (TestConfig.Browser.selenoidEnableVnc) {
                        addExposedPort(7900)
                        addExposedPort(4444)
                    }

                    waitingFor(
                        Wait.forHttp("/wd/hub/status")
                            .forPort(4444)
                            .forStatusCode(200)
                            .withStartupTimeout(Duration.ofSeconds(90))
                    )
                    withStartupTimeout(Duration.ofSeconds(90))

                    start()
                }
                println("✅ Container started successfully")
            } catch (e: Exception) {
                println("❌ Failed to start Testcontainers: ${e.message}")
                e.printStackTrace()
                throw e
            }
        }

        if (webDriverContainer!!.isRunning) {
            Configuration.remote = webDriverContainer!!.seleniumAddress.toString()
            Configuration.browserCapabilities = createCapabilities()
            println("🔴 Selenium Remote: ${Configuration.remote}")

            // Полезные ссылки для отладки
            if (TestConfig.Browser.selenoidEnableVnc) {
                println("🔴 VNC: http://localhost:${webDriverContainer!!.getMappedPort(7900)}/?password=secret")
            }
            println("🔴 Selenium UI: http://localhost:${webDriverContainer!!.getMappedPort(4444)}/ui/#/sessions")

        } else {
            error("Testcontainers WebDriver is not running!")
        }
    }

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
                "--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu",
                "--remote-allow-origins=*", "--disable-infobars",
                "--disable-notifications", "--disable-blink-features=AutomationControlled"
            )
            if (Configuration.headless) addArguments("--headless=new")
        }
        return DesiredCapabilities().apply {
            setCapability("browserName", "chrome")
            setCapability(CapabilityType.PAGE_LOAD_STRATEGY, PageLoadStrategy.NORMAL)
            setCapability(ChromeOptions.CAPABILITY, options)
        }
    }

    private fun createFirefoxCapabilities(): DesiredCapabilities {
        val options = FirefoxOptions().apply {
            if (Configuration.headless) addArguments("--headless")
        }
        return DesiredCapabilities().apply {
            setCapability("browserName", "firefox")
            setCapability(CapabilityType.PAGE_LOAD_STRATEGY, PageLoadStrategy.NORMAL)
            setCapability(FirefoxOptions.FIREFOX_OPTIONS, options)
        }
    }

    private fun createEdgeCapabilities(): DesiredCapabilities {
        val options = EdgeOptions().apply {
            addArguments("--no-sandbox", "--disable-dev-shm-usage", "--remote-allow-origins=*")
            if (Configuration.headless) addArguments("--headless=new")
        }
        return DesiredCapabilities().apply {
            setCapability("browserName", "MicrosoftEdge")
            setCapability(CapabilityType.PAGE_LOAD_STRATEGY, PageLoadStrategy.NORMAL)
            setCapability(EdgeOptions.CAPABILITY, options)
        }
    }

    private fun setupAllureLogger() {
        if (!SelenideLogger.hasListener("AllureSelenide")) {
            SelenideLogger.addListener("AllureSelenide", AllureSelenide().screenshots(true).savePageSource(false))
        }
    }

    fun teardown(closeContainer: Boolean = false) {
        println("🧹 BrowserConfig: cleaning up")
        try {
            Selenide.closeWebDriver() // Закрываем сессию браузера
        } catch (e: Exception) {
            println("⚠️ Error closing WebDriver: ${e.message}")
        }

        // Останавливаем контейнер только если это последний тест
        if (closeContainer) {
            try {
                webDriverContainer?.stop()
                webDriverContainer = null
                println("✅ Container stopped")
            } catch (e: Exception) {
                println("⚠️ Error stopping Testcontainers: ${e.message}")
            }
        }
    }
}
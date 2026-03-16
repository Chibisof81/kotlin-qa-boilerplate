package core.config

import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Configuration.*
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.logevents.SelenideLogger
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
    enum class RunMode { LOCAL, SELENOID, TESTCONTAINERS }

    private val logger = LoggerFactory.getLogger(BrowserConfig::class.java)
    private val tcLogger = LoggerFactory.getLogger("org.testcontainers")

    private val mode: RunMode by lazy {
        when (TestConfig.Browser.mode.lowercase()) {
            "selenoid" -> RunMode.SELENOID
            "testcontainers" -> RunMode.TESTCONTAINERS
            else -> RunMode.LOCAL
        }
    }

    @Volatile
    private var webDriverContainer: BrowserWebDriverContainer<*>? = null
    private val lock = Any()

    fun setup() {
        logger.info("🌐 BrowserConfig: starting in {} mode", mode)
        logger.info("Browser: {} {}", TestConfig.Browser.name, TestConfig.Browser.version)
        logger.info("Timeout: {}ms", TestConfig.Browser.timeout)
        logger.info("Headless: {}", TestConfig.Browser.headless && mode == RunMode.LOCAL)

        // ✅ ВАЖНО: Запустить контейнер ПЕРЕД конфигурацией Selenide
        when (mode) {
            RunMode.TESTCONTAINERS -> setupTestcontainers()
            RunMode.SELENOID -> {} // Selenoid уже запущен
            RunMode.LOCAL -> {} // Локальный браузер
        }

        // ✅ ТОЛЬКО ПОСЛЕ этого конфигурируем Selenide
        configureSelenide()
        setupAllureLogger()
    }

    private fun setupTestcontainers() {
        synchronized(lock) {
            if (webDriverContainer?.isRunning == true) {
                logger.info("Container already running")
                return
            }

            try {
                val imageName = buildImageName()
                logger.info("🐳 Starting Testcontainers with image: {}", imageName)

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

                logger.info("✅ Container started successfully")
                logContainerInfo()

            } catch (e: Exception) {
                cleanup()
                logger.error("❌ Failed to start Testcontainers", e)
                throw e
            }
        }
    }

    private fun configureSelenide() {
        Configuration().apply {
            timeout = TestConfig.Browser.timeout.toLong()
            browserSize = TestConfig.Browser.size
            headless = TestConfig.Browser.headless && mode == RunMode.LOCAL
            baseUrl = TestConfig.uiBaseUrl
            reportsFolder = "build/reports/tests"
            screenshots = true
            savePageSource = false

            // ✅ КЛЮЧЕВОЙ МОМЕНТ: Установить remote ПЕРЕД инициализацией браузера
            when (mode) {
                RunMode.TESTCONTAINERS -> {
                    remote = webDriverContainer!!.seleniumAddress.toString()
                    logger.info("🔴 Selenium Remote: {}", remote)
                }
                RunMode.SELENOID -> {
                    remote = TestConfig.Browser.selenoidUrl
                    browserVersion = TestConfig.Browser.version
                    logger.info("🔴 Selenoid URL: {}", remote)
                }
                RunMode.LOCAL -> {
                    remote = null  // Явно указываем локальный браузер
                    logger.info("🌐 Using local browser")
                }
            }

            browserCapabilities = createCapabilities()
        }
    }

    private fun buildImageName(): String {
        val browser = TestConfig.Browser.name.lowercase()
        val version = TestConfig.Browser.version
        return when (browser) {
            "chrome" -> "selenium/standalone-chrome:$version"
            "firefox" -> "selenium/standalone-firefox:$version"
            "edge" -> "selenium/standalone-edge:$version"
            else -> "selenium/standalone-chrome:$version"
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
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--remote-allow-origins=*",
                "--disable-infobars",
                "--disable-notifications",
                "--disable-blink-features=AutomationControlled"
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
            addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--remote-allow-origins=*"
            )
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
            SelenideLogger.addListener(
                "AllureSelenide",
                AllureSelenide().screenshots(true).savePageSource(false)
            )
        }
    }

    private fun logContainerInfo() {
        webDriverContainer?.let {
            logger.info("✅ Container started successfully")
            logger.info("🔴 Selenium Remote: {}", it.seleniumAddress)
            if (TestConfig.Browser.selenoidEnableVnc) {
                logger.info(
                    "🔴 VNC: http://localhost:{}/?password=secret",
                    it.getMappedPort(7900)
                )
            }
            logger.info(
                "🔴 Selenium UI: http://localhost:{}/ui/#/sessions",
                it.getMappedPort(4444)
            )
        }
    }

    fun teardown() {
        try {
            Selenide.closeWebDriver()
            logger.info("🧹 WebDriver closed")
        } catch (e: Exception) {
            logger.warn("Error closing WebDriver", e)
        } finally {
            cleanup()
        }
    }

    private fun cleanup() {
        synchronized(lock) {
            try {
                webDriverContainer?.stop()
                logger.info("✅ Container stopped")
            } catch (e: Exception) {
                logger.warn("Error stopping Testcontainers", e)
            } finally {
                webDriverContainer = null
            }
        }
    }
}

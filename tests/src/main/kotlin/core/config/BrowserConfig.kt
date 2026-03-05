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
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.BrowserWebDriverContainer
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import org.testcontainers.utility.MountableFile
import java.time.Duration

/**
 * Конфигурация браузера с поддержкой четырёх режимов:
 * - LOCAL           : локальный запуск (headless по умолчанию)
 * - SELENOID        : запуск во внешнем Selenoid Grid
 * - TESTCONTAINERS  : запуск в стандартном Testcontainers контейнере
 * - SELENOID_IN_TESTCONTAINERS : Selenoid, развёрнутый внутри Testcontainers
 *
 * Все настройки берутся из TestConfig.
 */
object BrowserConfig {

    enum class RunMode {
        LOCAL,
        SELENOID,
        TESTCONTAINERS,
        SELENOID_IN_TESTCONTAINERS  // ✅ Новый режим
    }

    // Режим запуска из конфига
    private val mode: RunMode by lazy {
        when (TestConfig.Browser.mode.lowercase()) {
            "selenoid" -> RunMode.SELENOID
            "testcontainers" -> RunMode.TESTCONTAINERS
            "selenoid-in-testcontainers", "selenoid_in_testcontainers" -> RunMode.SELENOID_IN_TESTCONTAINERS
            else -> RunMode.LOCAL
        }
    }

    // Контейнеры для разных режимов
    private var webDriverContainer: BrowserWebDriverContainer<*>? = null
    private var selenoidContainer: GenericContainer<*>? = null  // ✅ Для Selenoid в Testcontainers

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
        setupAllureLogger()

        // 3. Логирование
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
            RunMode.SELENOID_IN_TESTCONTAINERS -> setupSelenoidInTestcontainers()  // ✅ Новый метод
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
     * ✅ НОВЫЙ МЕТОД: Запуск Selenoid внутри Testcontainers
     *
     * Особенности:
     * - Поднимает полноценный Selenoid с поддержкой VNC/видео
     * - Требует наличия конфигурационных файлов в resources/selenoid/config
     * - Полностью изолирован для текущего запуска
     */
    private fun setupSelenoidInTestcontainers() {
        if (selenoidContainer == null) {
            println("📦 Запуск Selenoid в Testcontainers...")

            selenoidContainer = createSelenoidContainer().apply {
                start()
            }
        }

        val remoteUrl = "http://${selenoidContainer!!.host}:${selenoidContainer!!.getMappedPort(4444)}/wd/hub"
        Configuration.remote = remoteUrl
        Configuration.browserVersion = TestConfig.Browser.version

        val capabilities = createCapabilities().apply {
            setCapability("selenoid:options", mapOf(
                "enableVNC" to TestConfig.Browser.selenoidEnableVnc,
                "enableVideo" to TestConfig.Browser.selenoidEnableVideo,
                "enableLog" to TestConfig.Browser.selenoidEnableLog,
                "name" to "Kotlin Test Framework (Testcontainers)"
            ))
        }
        Configuration.browserCapabilities = capabilities

        println("✅ Selenoid в Testcontainers запущен")
        println("   Remote URL: $remoteUrl")

        if (TestConfig.Browser.selenoidEnableVnc) {
            println("   VNC доступен через Selenoid UI")
        }

        // Дополнительная информация для отладки
        println("   Для просмотра сессий: http://${selenoidContainer!!.host}:${selenoidContainer!!.getMappedPort(4444)}/status")
    }

    /**
     * Создаёт и настраивает контейнер с Selenoid
     */
    private fun createSelenoidContainer(): GenericContainer<*> {
        return GenericContainer(DockerImageName.parse("aerokube/selenoid:latest"))
            .withExposedPorts(4444)
            .withCopyFileToContainer(
                MountableFile.forClasspathResource("selenoid/config"),
                "/etc/selenoid"
            )
            .withFileSystemBind("/var/run/docker.sock", "/var/run/docker.sock", BindMode.READ_WRITE)
            .withCommand(
                "-conf", "/etc/selenoid/browsers.json",
                "-video-output-dir", "/opt/selenoid/video",
                "-timeout", "5m",
                "-service-timeout", "5m"
            )
            .waitingFor(
                Wait.forHttp("/status")
                    .forStatusCode(200)
                    .withStartupTimeout(Duration.ofSeconds(30))
            )
            .withStartupTimeout(Duration.ofMinutes(2))
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
     * Настройка Allure логгера (вынесено в отдельный метод)
     */
    private fun setupAllureLogger() {
        if (!SelenideLogger.hasListener("AllureSelenide")) {
            SelenideLogger.addListener(
                "AllureSelenide",
                AllureSelenide().screenshots(true).savePageSource(false)
            )
        }
    }

    /**
     * Завершение работы - вызывать после всех тестов
     */
    fun teardown() {
        println("🧹 BrowserConfig: cleaning up")

        try {
            Selenide.closeWebDriver()
        } catch (e: Exception) {
            println("⚠️ Error closing WebDriver: ${e.message}")
        }

        // Останавливаем обычный Testcontainers контейнер
        try {
            webDriverContainer?.stop()
            webDriverContainer = null
        } catch (e: Exception) {
            println("⚠️ Error stopping Testcontainers: ${e.message}")
        }

        // ✅ Останавливаем Selenoid контейнер
        try {
            selenoidContainer?.stop()
            selenoidContainer = null
        } catch (e: Exception) {
            println("⚠️ Error stopping Selenoid container: ${e.message}")
        }
    }
}
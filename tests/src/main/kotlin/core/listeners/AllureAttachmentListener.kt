package core.core.listeners

import com.codeborne.selenide.Selenide
import com.codeborne.selenide.WebDriverRunner
import core.config.EnvironmentConfig
import io.qameta.allure.Attachment
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.openqa.selenium.OutputType
import org.openqa.selenium.logging.LogType
import org.slf4j.LoggerFactory

/**
 * JUnit 5 Extension для прикрепления дополнительных артефактов к Allure отчёту.
 *
 * Что делает:
 * - При падении теста прикрепляет скриншот (если есть)
 * - Прикрепляет логи браузера
 * - Сохраняет page source
 * - Добавляет системную информацию в отчёт
 */
class AllureAttachmentListener : AfterTestExecutionCallback {

    private val log = LoggerFactory.getLogger(AllureAttachmentListener::class.java)

    companion object {
        @Attachment(value = "Скриншот при падении", type = "image/png")
        fun attachScreenshot(screenshot: ByteArray): ByteArray = screenshot

        @Attachment(value = "Page source", type = "text/html")
        fun attachPageSource(source: String): String = source

        @Attachment(value = "Логи браузера", type = "text/plain")
        fun attachBrowserLogs(logs: String): String = logs

        @Attachment(value = "Системная информация", type = "text/plain")
        fun attachSystemInfo(info: String): String = info
    }

    override fun afterTestExecution(context: ExtensionContext?) {
        val testFailed = context?.executionException?.isPresent ?: false

        if (testFailed) {
            // 1. Системная информация из нашего нового конфига
            attachSystemInfo(
                """

                |ОС: ${System.getProperty("os.name")}
                |Профиль: ${EnvironmentConfig.getProperty("env", "default")}
                |URL стенда: ${EnvironmentConfig.getProperty("ui.base.url", "N/A")}
                |Браузер: ${System.getProperty("selenide.browser", "chrome")}
                """.trimMargin()
            )

            // 2. Скриншот через Selenide (если он в стеке)
            // Это безопаснее, чем искать файлы в папках
            val screenshot = Selenide.screenshot(OutputType.BYTES)
            if (screenshot != null) {
                attachScreenshot(screenshot)
            }

            // 3. Логи браузера (Console Logs)
            val logs = Selenide.getWebDriverLogs(LogType.BROWSER).joinToString("\n")
            if (logs.isNotEmpty()) {
                attachBrowserLogs(logs)
            }

            // Внутри afterTestExecution, если тест упал:
            try {
                val currentUrl = WebDriverRunner.getWebDriver().currentUrl
                val pageTitle = WebDriverRunner.getWebDriver().title
                val pageContent = WebDriverRunner.source()

                val fullSource = "URL: $currentUrl\nTitle: $pageTitle\n\n$pageContent"
                attachPageSource(fullSource)
            } catch (e: Exception) {
                log.warn("Не удалось получить Page Source: ${e.message}")
            }
        }
    }
}
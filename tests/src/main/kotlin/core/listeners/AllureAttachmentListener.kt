package core.core.listeners

import io.qameta.allure.Attachment
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Paths

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
        context ?: return

        // Проверяем, упал ли тест
        val testFailed = context.executionException.isPresent

        if (testFailed) {
            log.info("📸 Тест упал, прикрепляем артефакты к Allure отчёту")

            // 1. Прикрепляем системную информацию
            attachSystemInfo(
                """
                |ОС: ${System.getProperty("os.name")}
                |Java: ${System.getProperty("java.version")}
                |Пользователь: ${System.getProperty("user.name")}
                |Рабочая директория: ${System.getProperty("user.dir")}
                |Активный профиль: ${System.getProperty("env", "default")}
                """.trimMargin()
            )

            // 2. Пытаемся найти скриншот (Selenide сохраняет в build/reports/tests)
            try {
                val screenshotDir = Paths.get("build/reports/tests/screenshots")
                if (Files.exists(screenshotDir)) {
                    Files.walk(screenshotDir)
                        .filter { Files.isRegularFile(it) && it.toString().endsWith(".png") }
                        .max { a, b ->
                            Files.getLastModifiedTime(a).compareTo(Files.getLastModifiedTime(b))
                        }
                        .ifPresent { latestScreenshot ->
                            val bytes = Files.readAllBytes(latestScreenshot)
                            attachScreenshot(bytes)
                            log.info("   Скриншот прикреплён: ${latestScreenshot.fileName}")
                        }
                }
            } catch (e: Exception) {
                log.warn("   Не удалось прикрепить скриншот: ${e.message}")
            }

            // 3. Пытаемся прикрепить page source (если есть)
            try {
                val sourceDir = Paths.get("build/reports/tests/page-source")
                if (Files.exists(sourceDir)) {
                    Files.walk(sourceDir)
                        .filter { Files.isRegularFile(it) && it.toString().endsWith(".html") }
                        .max { a, b ->
                            Files.getLastModifiedTime(a).compareTo(Files.getLastModifiedTime(b))
                        }
                        .ifPresent { latestSource ->
                            val content = Files.readString(latestSource)
                            attachPageSource(content)
                            log.info("   Page source прикреплён")
                        }
                }
            } catch (e: Exception) {
                log.warn("   Не удалось прикрепить page source: ${e.message}")
            }
        }
    }
}
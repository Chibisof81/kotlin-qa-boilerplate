package ui.regression

import com.codeborne.selenide.Selenide.open
import com.codeborne.selenide.Selenide.sleep
import core.core.listeners.RetryIfFailed
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import ui.BaseUiTest

class ExampleTest : BaseUiTest() {

    @Test
    @Tag("smoke")
    @RetryIfFailed(maxAttempts = 3)  // аннотация работает!
    fun `successful login`() {
        // Используем методы из BaseTest
        val email = randomEmail()  // из core!

        // Логирование из BaseTest
        log.info("Тестируем логин с email: $email")

        sleep(5000)

        // UI тест...
    }

    @Test
    fun openYa(){
        open("/")
        sleep(10000)
    }
}
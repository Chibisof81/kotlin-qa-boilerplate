package smoke

import base.BaseUiTest
import com.codeborne.selenide.Selenide.open
import com.codeborne.selenide.Selenide.sleep
import core.listeners.RetryIfFailed
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

class LoginTest : BaseUiTest() {

    @Test
    @Tag("smoke")
    @RetryIfFailed(maxAttempts = 3)  // аннотация работает!
    fun `successful login`() {
        // Используем методы из BaseTest
        val email = randomEmail()  // из core!

        // Логирование из BaseTest
        log.info("Тестируем логин с email: $email")

        // UI тест...
    }

    @Test
    fun openYa(){
        open("/")
        sleep(5000)
    }


}
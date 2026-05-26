package api.example

import api.spec.DefaultSpecs
import core.BaseApiTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Пример теста с использованием API клиента и спецификаций.
 */
@Epic("API Tests")
@Feature("Users")
class UsersApiTest : BaseApiTest() {

    private val usersClient = UsersApiClient()

    @Test
    @Story("Get Users")
    @DisplayName("Получить список пользователей")
    fun `get users - success`() {
        // Простой вариант с API клиентом
        val response = usersClient.getUsers()

        response.then()
            .spec(DefaultSpecs.successResponseSpec)

        assertEquals(200, response.statusCode)
    }

    @Test
    @Story("Create User")
    @DisplayName("Создать пользователя")
    fun `create user - success`() {
        val userBody = mapOf(
            "name" to "John Doe",
            "email" to randomEmail(),
            "role" to "user"
        )

        val response = usersClient.createUser(userBody)

        response.then()
            .spec(DefaultSpecs.strictSuccessResponseSpec)
            .body("name", org.hamcrest.Matchers.equalTo("John Doe"))
            .body("email", org.hamcrest.Matchers.containsString("@"))
    }

    @Test
    @Story("Get User")
    @DisplayName("Получить пользователя по ID")
    fun `get user by id - not found`() {
        val response = usersClient.getUserById("non-existent-id")

        response.then()
            .spec(DefaultSpecs.errorResponseSpec(404))
    }

    @Test
    @Story("Delete User")
    @DisplayName("Удалить пользователя с проверкой времени ответа")
    fun `delete user - check response time`() {
        val userBody = mapOf(
            "name" to "Temp User",
            "email" to randomEmail()
        )

        // Создаём пользователя для удаления
        val createResponse = usersClient.createUser(userBody)
        val userId = createResponse.jsonPath().getString("id")

        // Удаляем с проверкой времени ответа
        val deleteResponse = usersClient.deleteUser(userId!!)

        deleteResponse.then()
            .spec(DefaultSpecs.successResponseSpec)
            .spec(DefaultSpecs.timedResponseSpec(3000)) // max 3 seconds
    }
}

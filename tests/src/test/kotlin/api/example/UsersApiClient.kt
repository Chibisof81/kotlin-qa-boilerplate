package api.example

import api.client.ApiClient
import api.spec.DefaultSpecs
import io.restassured.response.Response

/**
 * Пример API клиента для работы с пользователями.
 * 
 * Наследует все настройки из api.config пакетов.
 */
class UsersApiClient : ApiClient() {
    
    override val basePath: String = "/api/v1"
    override val defaultSpec = DefaultSpecs.authorizedRequestSpec

    /**
     * Получить список пользователей.
     */
    fun getUsers(page: Int = 1, limit: Int = 20): Response {
        return get(
            path = "/users",
            queryParams = mapOf(
                "page" to page.toString(),
                "limit" to limit.toString()
            )
        )
    }

    /**
     * Получить пользователя по ID.
     */
    fun getUserById(userId: String): Response {
        return get(
            path = "/users/{id}",
            pathParams = mapOf("id" to userId)
        )
    }

    /**
     * Создать нового пользователя.
     */
    fun createUser(body: Map<String, Any>): Response {
        return post(
            path = "/users",
            body = body
        )
    }

    /**
     * Обновить пользователя.
     */
    fun updateUser(userId: String, body: Map<String, Any>): Response {
        return put(
            path = "/users/{id}",
            body = body,
            pathParams = mapOf("id" to userId)
        )
    }

    /**
     * Удалить пользователя.
     */
    fun deleteUser(userId: String): Response {
        return delete(
            path = "/users/{id}",
            pathParams = mapOf("id" to userId)
        )
    }
}

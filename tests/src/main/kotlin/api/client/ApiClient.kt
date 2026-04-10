package api.client

import api.config.ApiConfig
import api.spec.DefaultSpecs
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification

/**
 * Базовый API клиент с готовыми методами для распространённых операций.
 * 
 * Пример использования:
 * ```kotlin
 * class UserApiClient : ApiClient() {
 *     fun getUsers(): Response {
 *         return get("/users")
 *     }
 *     
 *     fun createUser(body: Map<String, Any>): Response {
 *         return post("/users", body)
 *     }
 * }
 * ```
 */
abstract class ApiClient {

    protected open val basePath: String = ""
    protected open val defaultSpec: RequestSpecification = DefaultSpecs.defaultRequestSpec

    init {
        // Инициализация REST Assured при первом обращении
        ApiConfig.resetRestAssured()
    }

    // === GET ===

    /**
     * Отправить GET запрос.
     */
    protected fun get(
        path: String,
        spec: RequestSpecification = defaultSpec,
        pathParams: Map<String, String> = emptyMap(),
        queryParams: Map<String, String> = emptyMap()
    ): Response {
        val request = RestAssured.given().spec(spec)

        pathParams.forEach { (key, value) ->
            request.pathParam(key, value)
        }

        queryParams.forEach { (key, value) ->
            request.queryParam(key, value)
        }

        return request
            .`when`()
            .get(fullPath(path))
    }

    // === POST ===

    /**
     * Отправить POST запрос с JSON телом.
     */
    protected fun post(
        path: String,
        body: Any,
        contentType: ContentType = ContentType.JSON,
        spec: RequestSpecification = defaultSpec,
        pathParams: Map<String, String> = emptyMap()
    ): Response {
        val request = RestAssured.given()
            .spec(spec)
            .contentType(contentType)

        pathParams.forEach { (key, value) ->
            request.pathParam(key, value)
        }

        return request
            .body(body)
            .`when`()
            .post(fullPath(path))
    }

    /**
     * Отправить POST запрос без тела.
     */
    protected fun post(
        path: String,
        spec: RequestSpecification = defaultSpec,
        pathParams: Map<String, String> = emptyMap()
    ): Response {
        val request = RestAssured.given().spec(spec)

        pathParams.forEach { (key, value) ->
            request.pathParam(key, value)
        }

        return request
            .`when`()
            .post(fullPath(path))
    }

    // === PUT ===

    /**
     * Отправить PUT запрос.
     */
    protected fun put(
        path: String,
        body: Any,
        contentType: ContentType = ContentType.JSON,
        spec: RequestSpecification = defaultSpec,
        pathParams: Map<String, String> = emptyMap()
    ): Response {
        val request = RestAssured.given()
            .spec(spec)
            .contentType(contentType)

        pathParams.forEach { (key, value) ->
            request.pathParam(key, value)
        }

        return request
            .body(body)
            .`when`()
            .put(fullPath(path))
    }

    // === PATCH ===

    /**
     * Отправить PATCH запрос.
     */
    protected fun patch(
        path: String,
        body: Any,
        contentType: ContentType = ContentType.JSON,
        spec: RequestSpecification = defaultSpec,
        pathParams: Map<String, String> = emptyMap()
    ): Response {
        val request = RestAssured.given()
            .spec(spec)
            .contentType(contentType)

        pathParams.forEach { (key, value) ->
            request.pathParam(key, value)
        }

        return request
            .body(body)
            .`when`()
            .patch(fullPath(path))
    }

    // === DELETE ===

    /**
     * Отправить DELETE запрос.
     */
    protected fun delete(
        path: String,
        spec: RequestSpecification = defaultSpec,
        pathParams: Map<String, String> = emptyMap()
    ): Response {
        val request = RestAssured.given().spec(spec)

        pathParams.forEach { (key, value) ->
            request.pathParam(key, value)
        }

        return request
            .`when`()
            .delete(fullPath(path))
    }

    // === Helpers ===

    /**
     * Построить полный путь к ресурсу.
     */
    private fun fullPath(path: String): String {
        return if (path.startsWith("/")) {
            basePath + path
        } else {
            "$basePath/$path"
        }
    }
}

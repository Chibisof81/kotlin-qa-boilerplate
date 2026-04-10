package api.spec

import io.restassured.specification.RequestSpecification
import io.restassured.specification.ResponseSpecification

/**
 * Готовые спецификации для быстрого использования в тестах.
 * 
 * Пример использования:
 * ```kotlin
 * given(DefaultSpecs.defaultRequestSpec)
 *     .`when`().get("/users")
 *     .then().spec(DefaultSpecs.successResponseSpec)
 * ```
 */
object DefaultSpecs {

    // === Request Specifications ===

    /**
     * Базовая спецификация для JSON запросов.
     * - Content-Type: application/json
     * - Accept: application/json
     * - Логирование request/response
     */
    val defaultRequestSpec: RequestSpecification by lazy {
        RequestSpecBuilder.buildDefaultSpec()
    }

    /**
     * Спецификация с авторизацией.
     * Тип авторизации берётся из AuthConfig.
     */
    val authorizedRequestSpec: RequestSpecification by lazy {
        RequestSpecBuilder.buildAuthorizedSpec()
    }

    /**
     * Спецификация с кастомным токеном.
     */
    fun authorizedWithTokenSpec(token: String): RequestSpecification {
        return RequestSpecBuilder.buildAuthorizedSpec(token)
    }

    /**
     * Спецификация для multipart/form-data (загрузка файлов).
     */
    val multipartRequestSpec: RequestSpecification by lazy {
        RequestSpecBuilder.buildMultipartSpec()
    }

    /**
     * Спецификация для application/x-www-form-urlencoded.
     */
    val formUrlEncodedRequestSpec: RequestSpecification by lazy {
        RequestSpecBuilder.buildFormUrlEncodedSpec()
    }

    /**
     * Спецификация с кастомными headers.
     */
    fun customHeadersRequestSpec(headers: Map<String, String>): RequestSpecification {
        return RequestSpecBuilder.buildWithHeaders(headers)
    }

    // === Response Specifications ===

    /**
     * Базовая спецификация без валидаций.
     */
    val defaultResponseSpec: ResponseSpecification by lazy {
        ResponseSpecBuilder.buildDefaultSpec()
    }

    /**
     * Спецификация для успешных ответов (statusCode < 400).
     */
    val successResponseSpec: ResponseSpecification by lazy {
        ResponseSpecBuilder.buildSuccessSpec()
    }

    /**
     * Строгая спецификация: 200 + JSON + response time.
     */
    val strictSuccessResponseSpec: ResponseSpecification by lazy {
        ResponseSpecBuilder.buildStrictSpec()
    }

    /**
     * Спецификация для ошибок с проверкой статус-кода.
     */
    fun errorResponseSpec(statusCode: Int): ResponseSpecification {
        return ResponseSpecBuilder.buildErrorSpec(statusCode)
    }

    /**
     * Спецификация с валидацией JSON схемы.
     */
    fun jsonSchemaResponseSpec(schemaPath: String): ResponseSpecification {
        return ResponseSpecBuilder.buildWithJsonSchemaSpec(schemaPath)
    }

    /**
     * Спецификация с проверкой времени ответа.
     */
    fun timedResponseSpec(maxTimeMs: Long): ResponseSpecification {
        return ResponseSpecBuilder.buildWithTimeSpec(maxTimeMs)
    }
}

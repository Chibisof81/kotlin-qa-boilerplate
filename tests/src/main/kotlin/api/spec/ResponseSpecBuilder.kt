package api.spec

import api.config.ApiConfig
import io.restassured.builder.ResponseSpecBuilder
import io.restassured.http.ContentType
import io.restassured.module.jsv.JsonSchemaValidator
import org.hamcrest.Matchers

/**
 * Builder для создания ResponseSpecification с предустановленными валидациями.
 */
object ResponseSpecBuilder {

    /**
     * Создать базовую спецификацию без валидаций.
     */
    fun buildDefaultSpec(): io.restassured.specification.ResponseSpecification {
        return ResponseSpecBuilder().build()
    }

    /**
     * Создать спецификацию с ожиданием успешного ответа (statusCode < 400).
     */
    fun buildSuccessSpec(): io.restassured.specification.ResponseSpecification {
        return ResponseSpecBuilder()
            .expectStatusCode(Matchers.lessThan(400))
            .build()
    }

    /**
     * Создать спецификацию с ожиданием конкретного статус-кода.
     */
    fun buildStatusSpec(expectedStatusCode: Int): io.restassured.specification.ResponseSpecification {
        return ResponseSpecBuilder()
            .expectStatusCode(expectedStatusCode)
            .build()
    }

    /**
     * Создать спецификацию с валидацией Content-Type.
     */
    fun buildWithContentTypeSpec(contentType: ContentType = ContentType.JSON): io.restassured.specification.ResponseSpecification {
        return ResponseSpecBuilder()
            .expectContentType(contentType)
            .build()
    }

    /**
     * Создать спецификацию с валидацией времени ответа.
     */
    fun buildWithTimeSpec(maxTimeMs: Long = ApiConfig.maxResponseTimeMs): io.restassured.specification.ResponseSpecification {
        return ResponseSpecBuilder()
            .expectResponseTime(Matchers.lessThan(maxTimeMs), java.util.concurrent.TimeUnit.MILLISECONDS)
            .build()
    }

    /**
     * Создать спецификацию с валидацией JSON схемы.
     */
    fun buildWithJsonSchemaSpec(schemaPath: String): io.restassured.specification.ResponseSpecification {
        return ResponseSpecBuilder()
            .expectBody(JsonSchemaValidator.matchesJsonSchemaInClasspath(schemaPath))
            .build()
    }

    /**
     * Создать спецификацию с проверкой конкретного поля в JSON.
     */
    fun buildWithFieldSpec(jsonPath: String, matcher: org.hamcrest.Matcher<*>): io.restassured.specification.ResponseSpecification {
        return ResponseSpecBuilder()
            .expectBody(jsonPath, matcher)
            .build()
    }

    /**
     * Создать спецификацию для ошибки (4xx или 5xx).
     */
    fun buildErrorSpec(expectedStatusCode: Int): io.restassured.specification.ResponseSpecification {
        return ResponseSpecBuilder()
            .expectStatusCode(expectedStatusCode)
            .expectContentType(ContentType.JSON)
            .build()
    }

    /**
     * Комплексная спецификация: success + content-type + response time.
     */
    fun buildStrictSpec(
        expectedStatusCode: Int = 200,
        contentType: ContentType = ContentType.JSON,
        maxResponseTimeMs: Long = ApiConfig.maxResponseTimeMs,
        jsonSchemaPath: String? = null
    ): io.restassured.specification.ResponseSpecification {
        val builder = ResponseSpecBuilder()
            .expectStatusCode(expectedStatusCode)
            .expectContentType(contentType)
            .expectResponseTime(Matchers.lessThan(maxResponseTimeMs), java.util.concurrent.TimeUnit.MILLISECONDS)

        if (jsonSchemaPath != null) {
            builder.expectBody(JsonSchemaValidator.matchesJsonSchemaInClasspath(jsonSchemaPath))
        }

        return builder.build()
    }
}

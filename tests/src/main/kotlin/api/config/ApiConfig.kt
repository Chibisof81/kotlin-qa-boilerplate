package api.config

import core.config.TestConfig
import io.restassured.RestAssured

/**
 * Конфигурация для API тестов.
 * Наследует базовые URL из TestConfig и добавляет REST Assured специфичные настройки.
 */
object ApiConfig {

    // === URLs (наследуются из TestConfig) ===
    val baseUrl: String = TestConfig.apiBaseUrl
    val basePath: String = TestConfig.apiBasePath
    val fullBaseUrl: String by lazy {
        if (basePath.isNotEmpty() && basePath.isNotBlank()) {
            "$baseUrl$basePath"
        } else {
            baseUrl
        }
    }

    // === Timeouts ===
    val connectionTimeoutMs: Int = System.getProperty("api.connection.timeout", "5000").toInt()
    val readTimeoutMs: Int = System.getProperty("api.read.timeout", "10000").toInt()

    // === Retries ===
    val maxRetries: Int = System.getProperty("api.max.retries", "0").toInt()
    val retryDelayMs: Long = System.getProperty("api.retry.delay", "1000").toLong()

    // === Logging ===
    val enableRequestLogging: Boolean = System.getProperty("api.log.request", "true").toBoolean()
    val enableResponseLogging: Boolean = System.getProperty("api.log.response", "true").toBoolean()
    val logDetail: RequestLogDetail = when (System.getProperty("api.log.detail", "BODY").uppercase()) {
        "NONE" -> RequestLogDetail.NONE
        "HEADERS" -> RequestLogDetail.HEADERS
        "BODY" -> RequestLogDetail.BODY
        "ALL" -> RequestLogDetail.ALL
        "PARAMS" -> RequestLogDetail.PARAMS
        "PATH" -> RequestLogDetail.PATH
        else -> RequestLogDetail.BODY
    }

    enum class RequestLogDetail {
        NONE, HEADERS, BODY, ALL, PARAMS, PATH
    }

    // === Validation ===
    val defaultContentType: String = System.getProperty("api.content.type", "application/json")
    val expectSuccessByDefault: Boolean = System.getProperty("api.expect.success", "false").toBoolean()
    val maxResponseTimeMs: Long = System.getProperty("api.max.response.time", "5000").toLong()

    /**
     * Сбросить REST Assured к настройкам по умолчанию.
     * Полезно использовать перед запуском тестов.
     */
    fun resetRestAssured() {
        RestAssured.reset()
        RestAssured.baseURI = baseUrl
        RestAssured.basePath = basePath
        RestAssured.port = RestAssured.UNDEFINED_PORT
    }
}

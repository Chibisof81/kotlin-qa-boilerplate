package api.spec

import api.config.ApiConfig
import api.config.AuthConfig
import api.config.AuthConfig.AuthType
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.config.HttpClientConfig
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification

/**
 * Builder для создания RequestSpecification с предустановленными настройками.
 */
object RequestSpecBuilder {

    /**
     * Создать базовую спецификацию с настройками по умолчанию.
     */
    fun buildDefaultSpec(): RequestSpecification {
        return createBaseBuilder()
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .build()
    }

    /**
     * Создать спецификацию с авторизацией.
     */
    fun buildAuthorizedSpec(token: String? = null): RequestSpecification {
        val builder = createBaseBuilder()
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)

        when (AuthConfig.authType) {
            AuthType.BASIC -> builder.setAuth(
                RestAssured.preemptive()
                    .basic(AuthConfig.username, AuthConfig.password)
            )
            AuthType.BEARER -> builder.addHeader(
                "Authorization",
                "Bearer ${token ?: AuthConfig.token}"
            )
            AuthType.API_KEY -> builder.addHeader(
                AuthConfig.apiKeyHeader,
                AuthConfig.apiKey
            )
            AuthType.OAUTH2 -> builder.setAuth(
                RestAssured.oauth2(token ?: AuthConfig.token)
            )
            AuthType.NONE -> {} // No auth
        }

        return builder.build()
    }

    /**
     * Создать спецификацию для multipart/form-data (загрузка файлов).
     */
    fun buildMultipartSpec(): RequestSpecification {
        return createBaseBuilder()
            .setContentType(ContentType.MULTIPART)
            .build()
    }

    /**
     * Создать спецификацию с кастомными headers.
     */
    fun buildWithHeaders(headers: Map<String, String>): RequestSpecification {
        val builder = createBaseBuilder()
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)

        headers.forEach { (key, value) ->
            builder.addHeader(key, value)
        }

        return builder.build()
    }

    /**
     * Создать спецификацию для URL-Encoded форм.
     */
    fun buildFormUrlEncodedSpec(): RequestSpecification {
        return createBaseBuilder()
            .setContentType(ContentType.URLENC)
            .build()
    }

    /**
     * Создать спецификацию с кастомным Content-Type.
     */
    fun buildWithContentType(contentType: ContentType): RequestSpecification {
        return createBaseBuilder()
            .setContentType(contentType)
            .build()
    }

    /**
     * Базовый builder с общими настройками.
     */
    private fun createBaseBuilder(): RequestSpecBuilder {
        val httpClientConfig = HttpClientConfig.httpClientConfig()
            .setParam("http.connection.timeout", ApiConfig.connectionTimeoutMs)
            .setParam("http.socket.timeout", ApiConfig.readTimeoutMs)

        val builder = RequestSpecBuilder()
            .setBaseUri(ApiConfig.fullBaseUrl)
            .setConfig(io.restassured.config.RestAssuredConfig.config().httpClient(httpClientConfig))

        // Logging
        if (ApiConfig.enableRequestLogging) {
            builder.addFilter(RequestLoggingFilter())
        }
        if (ApiConfig.enableResponseLogging) {
            builder.addFilter(ResponseLoggingFilter())
        }

        return builder
    }
}

package api.config

/**
 * Конфигурация для авторизации в API тестах.
 */
object AuthConfig {

    // === Auth Type ===
    val authType: AuthType = when (System.getProperty("api.auth.type", "NONE").uppercase()) {
        "BASIC" -> AuthType.BASIC
        "BEARER" -> AuthType.BEARER
        "API_KEY" -> AuthType.API_KEY
        "OAUTH2" -> AuthType.OAUTH2
        else -> AuthType.NONE
    }

    // === Credentials ===
    val username: String = System.getProperty("api.auth.username", "")
    val password: String = System.getProperty("api.auth.password", "")
    val token: String = System.getProperty("api.auth.token", "")
    val apiKey: String = System.getProperty("api.auth.apikey", "")
    val apiKeyHeader: String = System.getProperty("api.auth.apikey.header", "X-API-Key")

    // === OAuth2 ===
    val oauth2TokenUrl: String = System.getProperty("api.auth.oauth2.token.url", "")
    val oauth2ClientId: String = System.getProperty("api.auth.oauth2.client.id", "")
    val oauth2ClientSecret: String = System.getProperty("api.auth.oauth2.client.secret", "")
    val oauth2Scope: String = System.getProperty("api.auth.oauth2.scope", "")

    // === Token Endpoint ===
    val tokenEndpoint: String = System.getProperty("api.auth.token.endpoint", "/auth/login")

    enum class AuthType {
        NONE, BASIC, BEARER, API_KEY, OAUTH2
    }

    /**
     * Проверка, настроена ли авторизация.
     */
    fun isAuthConfigured(): Boolean = authType != AuthType.NONE && when (authType) {
        AuthType.BASIC -> username.isNotEmpty() && password.isNotEmpty()
        AuthType.BEARER -> token.isNotEmpty()
        AuthType.API_KEY -> apiKey.isNotEmpty()
        AuthType.OAUTH2 -> oauth2TokenUrl.isNotEmpty() && oauth2ClientId.isNotEmpty()
        AuthType.NONE -> false
    }
}

package config

/**
 * Удобный доступ ко всем конфигурационным параметрам тестов.
 * Все значения загружаются через EnvironmentConfig.
 */
object TestConfig {

    // === Общие настройки ===
    val env: String by lazy { EnvironmentConfig.getProperty("env", "default") }

    // === Настройки окружений ===
    val apiBaseUrl: String by lazy { EnvironmentConfig.getProperty("api.base.url") }
    val apiBasePath: String by lazy { EnvironmentConfig.getProperty("api.base.path", "") }

    val uiBaseUrl: String by lazy { EnvironmentConfig.getProperty("ui.base.url") }

    val dbUrl: String? by lazy { EnvironmentConfig.getPropertyOrNull("db.url") }
    val dbUser: String? by lazy { EnvironmentConfig.getPropertyOrNull("db.user") }
    val dbPassword: String? by lazy { EnvironmentConfig.getPropertyOrNull("db.password") }

    // === Настройки браузера ===
    object Browser {
        val mode: String by lazy { EnvironmentConfig.getProperty("browser.mode", "local") }
        val name: String by lazy { EnvironmentConfig.getProperty("browser.name", "chrome") }
        val version: String by lazy { EnvironmentConfig.getProperty("browser.version", "latest") }
        val timeout: Long by lazy { EnvironmentConfig.getProperty("browser.timeout", "15000").toLong() }
        val size: String by lazy { EnvironmentConfig.getProperty("browser.size", "1920x1080") }
        val headless: Boolean by lazy { EnvironmentConfig.getProperty("browser.headless", "true").toBoolean() }
        val selenoidUrl: String by lazy { EnvironmentConfig.getProperty("browser.selenoid.url", "http://localhost:4444/wd/hub") }

        // Дополнительные опции для Selenoid
        val selenoidEnableVnc: Boolean by lazy { EnvironmentConfig.getProperty("browser.selenoid.vnc", "true").toBoolean() }
        val selenoidEnableVideo: Boolean by lazy { EnvironmentConfig.getProperty("browser.selenoid.video", "false").toBoolean() }
        val selenoidEnableLog: Boolean by lazy { EnvironmentConfig.getProperty("browser.selenoid.log", "true").toBoolean() }
    }
}
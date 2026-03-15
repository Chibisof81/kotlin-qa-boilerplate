package core.config

import java.io.InputStream
import java.util.Properties

/**
 * Загружает конфигурацию из properties-файлов в зависимости от активного профиля.
 *
 * Поддерживает:
 * - Дефолтный файл: application-default.properties
 * - Профильные файлы: application-{profile}.properties
 * - Системные свойства (переопределяют всё)
 *
 * Профиль задаётся через: -Denv=stage или системную переменную ENV
 */
object EnvironmentConfig {

    private const val DEFAULT_PROFILE = "default"
    private val profiles: List<String> = loadProfiles()
    private val properties: Properties = loadProperties()

    init {
        val criticalProps = listOf("ui.base.url", "api.token")
        criticalProps.forEach { prop ->
            if (!hasProperty(prop)) {
                // Мы сразу поймем, что забыли прокинуть переменную
                println("WARNING: Critical property '$prop' is missing!")
            }
        }
        // Полезно для логов в CI/CD
        println("--- CONFIG LOADED ---")
        println("Active Profiles: $profiles")
        println("Target UI URL: ${getProperty("ui.base.url")}")
        println("---------------------")
    }

    /**
     * Возвращает значение свойства по ключу.
     * Сначала ищет в System.getProperty (передано через -D),
     * затем в загруженных properties-файлах.
     */
    fun getProperty(key: String): String {
        // 1. Проверяем System Properties (наивысший приоритет)
        System.getProperty(key)?.let { return it }

        // 2. Проверяем Environment Variables (UI_BASE_URL)
        // Преобразуем точку в подчеркивание и в верхний регистр
        val envKey = key.replace(".", "_").uppercase()
        println("DEBUG getProperty key=$key envKey=$envKey env=${System.getenv(envKey)} sys=${System.getProperty(key)}")
        System.getenv(envKey)?.let { return it }

        // 3. Ищем в загруженных файлах
        return properties.getProperty(key)
            ?: error("Property '$key' not found in any config file or system properties")
    }

    /**
     * Возвращает свойство со значением по умолчанию, если не найдено.
     */
    fun getProperty(key: String, defaultValue: String): String {
        return getPropertyOrNull(key) ?: defaultValue
    }

    /**
     * Возвращает свойство или null, если не найдено.
     */
    fun getPropertyOrNull(key: String): String? {
        System.getProperty(key)?.let { return it }

        // ✅ Добавить проверку Environment Variables
        val envKey = key.replace(".", "_").uppercase()
        System.getenv(envKey)?.let { return it }

        return properties.getProperty(key)
    }

    /**
     * Проверяет, существует ли свойство.
     */
    fun hasProperty(key: String): Boolean {
        val envKey = key.replace(".", "_").uppercase()
        return System.getProperty(key) != null
                || System.getenv(envKey) != null
                || properties.containsKey(key)
    }

    private fun loadProfiles(): List<String> {
        // Сначала проверяем System Property, потом переменную окружения, иначе default
        val env = System.getProperty("env")
            ?: System.getenv("ENV")
            ?: DEFAULT_PROFILE

        return env.split(",").map { it.trim() }.ifEmpty { listOf(DEFAULT_PROFILE) }
    }

    private fun loadProperties(): Properties {
        val props = Properties()

        // 1. Загружаем default конфиг (всегда)
        loadPropertiesFile("application-$DEFAULT_PROFILE.properties")?.let {
            props.load(it)
            it.close()
        }

        // 2. Загружаем конфиги для каждого профиля (последующие переопределяют предыдущие)
        profiles.forEach { profile ->
            if (profile != DEFAULT_PROFILE) {
                loadPropertiesFile("application-$profile.properties")?.let {
                    props.load(it)
                    it.close()
                }
            }
        }

        return props
    }

    private fun loadPropertiesFile(fileName: String): InputStream? {
        return this::class.java.classLoader.getResourceAsStream("config/$fileName")
            ?: this::class.java.classLoader.getResourceAsStream(fileName)
    }
}
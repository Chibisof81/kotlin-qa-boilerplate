plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.allure)
    id("java-test-fixtures") // Для BaseTest
}

repositories {
    mavenCentral()
}

dependencies {
    // Test libs как api для testFixtures
    api(libs.junit.api)
    api(libs.junit.params)
    api(libs.junit.engine) // engine только для runtime

    api(libs.testcontainers.core)
    api(libs.testcontainers.junit)
    api(libs.testcontainers.selenium)

    api(libs.allure.selenide)
    api(libs.selenide)

    api(libs.slf4j.api)
    api(libs.logback.classic)
    api(libs.aspectjweaver)

    api(libs.commons.configuration)
    api(libs.kotlin.reflect)

    // REST Assured
    api(libs.restassured)
    api(libs.restassured.json.schema)
}

java {
    withSourcesJar()
    withJavadocJar()
}

artifacts {
    archives(tasks.named("sourcesJar"))
    archives(tasks.named("javadocJar"))
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
    systemProperty("junit.jupiter.execution.parallel.enabled", "true")
    systemProperty("junit.jupiter.execution.parallel.config.strategy", "dynamic")
    systemProperty("junit.jupiter.extensions.autodetection.enabled", "true")

    // Пробрасываем все системные свойства (переданные через -Dkey=value)
    systemProperties(System.getProperties().mapKeys { it.key.toString() })

    // Если вы хотите явно пробросить переменные окружения (Environment Variables)
    // Gradle делает это автоматически для текущего процесса,
    // но для надежности в некоторых CI это прописывают так:
    environment(System.getenv())
}

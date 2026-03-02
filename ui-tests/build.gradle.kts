plugins {
    alias(libs.plugins.kotlin.jvm)
    id("java-test-fixtures") // Обязательно для доступа к testFixturesImplementation!
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Core main + test fixtures (BaseTest)
    implementation(project(":core"))
    testFixturesImplementation(project(":core"))
    // Ничего больше не нужно - наследуется от core
    testImplementation(project(":core"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

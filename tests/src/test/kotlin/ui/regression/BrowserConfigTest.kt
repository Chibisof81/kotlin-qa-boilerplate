package ui.regression

import com.codeborne.selenide.Condition.visible
import com.codeborne.selenide.Selenide.*
import core.core.listeners.RetryIfFailed
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import ui.BaseUiTest

class BrowserConfigTest : BaseUiTest() {

    // ========================================================================
    // LOCAL MODE — браузер запускается на локальной машине
    // ========================================================================

    /**
     * Запуск:
     *   Gradle:       ./gradlew test -Dtags=local,chrome -Dbrowser.mode=local -Dbrowser.name=chrome
     *   Env vars:     BROWSER_MODE=local  BROWSER_NAME=chrome
     *   IntelliJ:     Run Config → VM options: -Dbrowser.mode=local -Dbrowser.name=chrome
     *                 или Environment variables: BROWSER_MODE=local;BROWSER_NAME=chrome
     */
    @Test
    @Tag("local")
    @Tag("chrome")
    @Tag("browser-config")
    @RetryIfFailed(maxAttempts = 2)
    fun `local chrome - open main page`() {
        open("/")
        `$`("body").shouldBe(visible)
        log.info("Local Chrome: main page loaded successfully")
    }

    /**
     * Запуск:
     *   Gradle:       ./gradlew test -Dtags=local,firefox -Dbrowser.mode=local -Dbrowser.name=firefox
     *   Env vars:     BROWSER_MODE=local  BROWSER_NAME=firefox
     *   IntelliJ:     Run Config → VM options: -Dbrowser.mode=local -Dbrowser.name=firefox
     *                 или Environment variables: BROWSER_MODE=local;BROWSER_NAME=firefox
     */
    @Test
    @Tag("local")
    @Tag("firefox")
    @Tag("browser-config")
    @RetryIfFailed(maxAttempts = 2)
    fun `local firefox - open main page`() {
        open("/")
        `$`("body").shouldBe(visible)
        log.info("Local Firefox: main page loaded successfully")
    }

    /**
     * Запуск:
     *   Gradle:       ./gradlew test -Dtags=local,edge -Dbrowser.mode=local -Dbrowser.name=edge
     *   Env vars:     BROWSER_MODE=local  BROWSER_NAME=edge
     *   IntelliJ:     Run Config → VM options: -Dbrowser.mode=local -Dbrowser.name=edge
     *                 или Environment variables: BROWSER_MODE=local;BROWSER_NAME=edge
     */
    @Test
    @Tag("local")
    @Tag("edge")
    @Tag("browser-config")
    @RetryIfFailed(maxAttempts = 2)
    fun `local edge - open main page`() {
        open("/")
        `$`("body").shouldBe(visible)
        log.info("Local Edge: main page loaded successfully")
    }

    // ========================================================================
    // SELENOID MODE — браузер на удалённом Selenoid-сервере
    // ========================================================================

    /**
     * Запуск:
     *   Gradle:       ./gradlew test -Dtags=selenoid,chrome -Dbrowser.mode=selenoid -Dbrowser.name=chrome -Dbrowser.selenoid.url=http://localhost:4444/wd/hub
     *   Env vars:     BROWSER_MODE=selenoid  BROWSER_NAME=chrome  BROWSER_SELENOID_URL=http://localhost:4444/wd/hub
     *   IntelliJ:     Run Config → VM options: -Dbrowser.mode=selenoid -Dbrowser.name=chrome -Dbrowser.selenoid.url=http://localhost:4444/wd/hub
     *                 или Environment variables: BROWSER_MODE=selenoid;BROWSER_NAME=chrome;BROWSER_SELENOID_URL=http://localhost:4444/wd/hub
     */
    @Test
    @Tag("selenoid")
    @Tag("chrome")
    @Tag("browser-config")
    @RetryIfFailed(maxAttempts = 2)
    fun `selenoid chrome - open main page`() {
        open("/")
        `$`("body").shouldBe(visible)
        log.info("Selenoid Chrome: main page loaded successfully")
    }

    /**
     * Запуск:
     *   Gradle:       ./gradlew test -Dtags=selenoid,firefox -Dbrowser.mode=selenoid -Dbrowser.name=firefox -Dbrowser.selenoid.url=http://localhost:4444/wd/hub
     *   Env vars:     BROWSER_MODE=selenoid  BROWSER_NAME=firefox  BROWSER_SELENOID_URL=http://localhost:4444/wd/hub
     *   IntelliJ:     Run Config → VM options: -Dbrowser.mode=selenoid -Dbrowser.name=firefox -Dbrowser.selenoid.url=http://localhost:4444/wd/hub
     *                 или Environment variables: BROWSER_MODE=selenoid;BROWSER_NAME=firefox;BROWSER_SELENOID_URL=http://localhost:4444/wd/hub
     */
    @Test
    @Tag("selenoid")
    @Tag("firefox")
    @Tag("browser-config")
    @RetryIfFailed(maxAttempts = 2)
    fun `selenoid firefox - open main page`() {
        open("/")
        `$`("body").shouldBe(visible)
        log.info("Selenoid Firefox: main page loaded successfully")
    }

    /**
     * Запуск:
     *   Gradle:       ./gradlew test -Dtags=selenoid,edge -Dbrowser.mode=selenoid -Dbrowser.name=edge -Dbrowser.selenoid.url=http://localhost:4444/wd/hub
     *   Env vars:     BROWSER_MODE=selenoid  BROWSER_NAME=edge  BROWSER_SELENOID_URL=http://localhost:4444/wd/hub
     *   IntelliJ:     Run Config → VM options: -Dbrowser.mode=selenoid -Dbrowser.name=edge -Dbrowser.selenoid.url=http://localhost:4444/wd/hub
     *                 или Environment variables: BROWSER_MODE=selenoid;BROWSER_NAME=edge;BROWSER_SELENOID_URL=http://localhost:4444/wd/hub
     */
    @Test
    @Tag("selenoid")
    @Tag("edge")
    @Tag("browser-config")
    @RetryIfFailed(maxAttempts = 2)
    fun `selenoid edge - open main page`() {
        open("/")
        `$`("body").shouldBe(visible)
        log.info("Selenoid Edge: main page loaded successfully")
    }

    // ========================================================================
    // TESTCONTAINERS MODE — браузер в Docker-контейнере (selenium/standalone-*)
    // ========================================================================

    /**
     * Запуск:
     *   Gradle:       ./gradlew test -Dtags=testcontainers,chrome -Dbrowser.mode=testcontainers -Dbrowser.name=chrome
     *   Env vars:     BROWSER_MODE=testcontainers  BROWSER_NAME=chrome
     *   IntelliJ:     Run Config → VM options: -Dbrowser.mode=testcontainers -Dbrowser.name=chrome
     *                 или Environment variables: BROWSER_MODE=testcontainers;BROWSER_NAME=chrome
     *   Примечание:   Требуется установленный и запущенный Docker
     */
    @Test
    @Tag("testcontainers")
    @Tag("chrome")
    @Tag("browser-config")
    @RetryIfFailed(maxAttempts = 2)
    fun `testcontainers chrome - open main page`() {
        open("/")
        `$`("body").shouldBe(visible)
        log.info("Testcontainers Chrome: main page loaded successfully")
    }

    /**
     * Запуск:
     *   Gradle:       ./gradlew test -Dtags=testcontainers,firefox -Dbrowser.mode=testcontainers -Dbrowser.name=firefox
     *   Env vars:     BROWSER_MODE=testcontainers  BROWSER_NAME=firefox
     *   IntelliJ:     Run Config → VM options: -Dbrowser.mode=testcontainers -Dbrowser.name=firefox
     *                 или Environment variables: BROWSER_MODE=testcontainers;BROWSER_NAME=firefox
     *   Примечание:   Требуется установленный и запущенный Docker
     */
    @Test
    @Tag("testcontainers")
    @Tag("firefox")
    @Tag("browser-config")
    @RetryIfFailed(maxAttempts = 2)
    fun `testcontainers firefox - open main page`() {
        open("/")
        `$`("body").shouldBe(visible)
        log.info("Testcontainers Firefox: main page loaded successfully")
    }

    /**
     * Запуск:
     *   Gradle:       ./gradlew test -Dtags=testcontainers,edge -Dbrowser.mode=testcontainers -Dbrowser.name=edge
     *   Env vars:     BROWSER_MODE=testcontainers  BROWSER_NAME=edge
     *   IntelliJ:     Run Config → VM options: -Dbrowser.mode=testcontainers -Dbrowser.name=edge
     *                 или Environment variables: BROWSER_MODE=testcontainers;BROWSER_NAME=edge
     *   Примечание:   Требуется установленный и запущенный Docker
     */
    @Test
    @Tag("testcontainers")
    @Tag("edge")
    @Tag("browser-config")
    @RetryIfFailed(maxAttempts = 2)
    fun `testcontainers edge - open main page`() {
        open("/")
        `$`("body").shouldBe(visible)
        log.info("Testcontainers Edge: main page loaded successfully")
    }
}

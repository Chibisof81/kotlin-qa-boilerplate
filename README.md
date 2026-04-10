# README - Kotlin QA Boilerplate

## Описание проекта

**Kotlin QA Boilerplate** — это готовый фреймворк для автоматизации UI-тестирования на Kotlin с поддержкой Selenide, Allure, TestContainers и Selenoid. Проект включает конфигурацию браузеров, слушатели событий, логирование и интеграцию с системами мониторинга.

---

## Полный список переменных и значений для переопределения

### Конфигурация браузера (BrowserConfig.kt)

| Переменная | Тип | Описание | Значение по умолчанию |
|---|---|---|---|
| `BROWSER_MODE` | String | Режим запуска браузера (LOCAL, SELENOID, TESTCONTAINERS) | `LOCAL` |
| `BROWSER_NAME` | String | Имя браузера (chrome, firefox, edge) | `chrome` |
| `BROWSER_VERSION` | String | Версия браузера | актуальная версия |
| `BROWSER_TIMEOUT` | Long | Таймаут для операций (мс) | `10000` |
| `BROWSER_HEADLESS` | Boolean | Запуск браузера в headless режиме | `false` |
| `BROWSER_SIZE` | String | Размер окна браузера (например, "1920x1080") | `1920x1080` |
| `UI_BASE_URL` | String | Базовый URL приложения для тестирования | `http://localhost:8080` |

---

### Конфигурация Selenoid (для удаленного запуска)

| Переменная | Тип | Описание | Значение по умолчанию |
|---|---|---|---|
| `SELENOID_URL` | String | URL Selenoid сервера | `http://localhost:4444` |
| `SELENOID_ENABLE_VNC` | Boolean | Включить VNC для просмотра сессии | `true` |

---

### Конфигурация TestContainers (для контейнеризованного запуска)

| Переменная | Тип | Описание | Значение по умолчанию |
|---|---|---|---|
| `SELENOID_ENABLE_VNC` | Boolean | Включить VNC в контейнере | `true` |
| Docker Image | String | Образ браузера (автоматически формируется) | `selenium/standalone-chrome:latest` |

---

### Конфигурация Selenide (Configuration)

| Переменная | Тип | Описание | Значение по умолчанию |
|---|---|---|---|
| `BROWSER_TIMEOUT` | Long | Таймаут ожидания элементов (мс) | `10000` |
| `BROWSER_SIZE` | String | Размер окна браузера | `1920x1080` |
| `BROWSER_HEADLESS` | Boolean | Headless режим | `false` |
| `UI_BASE_URL` | String | Базовый URL приложения | `http://localhost:8080` |
| `REPORTS_FOLDER` | String | Папка для отчетов | `build/reports/tests` |

---

### Опции браузеров (Chrome)

| Аргумент | Описание |
|---|---|
| `--no-sandbox` | Отключить sandbox режим |
| `--disable-dev-shm-usage` | Отключить использование /dev/shm |
| `--disable-gpu` | Отключить GPU ускорение |
| `--remote-allow-origins=*` | Разрешить удаленные источники |
| `--disable-infobars` | Отключить информационные панели |
| `--disable-notifications` | Отключить уведомления |
| `--disable-blink-features=AutomationControlled` | Скрыть признаки автоматизации |
| `--headless=new` | Headless режим (новая реализация) |

---

### Опции браузеров (Firefox)

| Аргумент | Описание |
|---|---|
| `--headless` | Headless режим |

---

### Опции браузеров (Edge)

| Аргумент | Описание |
|---|---|
| `--no-sandbox` | Отключить sandbox режим |
| `--disable-dev-shm-usage` | Отключить использование /dev/shm |
| `--remote-allow-origins=*` | Разрешить удаленные источники |
| `--headless=new` | Headless режим |

---

### Конфигурация Allure

| Переменная | Тип | Описание | Значение по умолчанию |
|---|---|---|---|
| `AllureSelenide.screenshots()` | Boolean | Сохранять скриншоты в Allure отчет | `true` |
| `AllureSelenide.savePageSource()` | Boolean | Сохранять исходный код страницы в отчет | `false` |

---

## Структура проекта

```
kotlin-qa-boilerplate/
├── tests/
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/
│   │   │   │   ├── core/
│   │   │   │   │   ├── config/
│   │   │   │   │   │   ├── BrowserConfig.kt        # Конфигурация браузеров
│   │   │   │   │   │   ├── EnvironmentConfig.kt    # Конфигурация окружений
│   │   │   │   │   │   └── TestConfig.kt           # Основные конфиги
│   │   │   │   │   ├── listeners/
│   │   │   │   │   │   ├── AllureAttachmentListener.kt
│   │   │   │   │   │   ├── RetryListener.kt
│   │   │   │   │   │   └── TestLifecycleLogger.kt
│   │   │   │   │   ├── BaseTest.kt                 # Базовый класс тестов
│   │   │   │   │   └── resources/
│   │   │   │   │       └── config/
│   │   │   │   │           ├── application-default.properties
│   │   │   │   │           ├── application-dev.properties
│   │   │   │   │           ├── application-prod.properties
│   │   │   │   │           └── application-stage.properties
│   │   │   │   ├── api/                            # API тестирование (REST Assured)
│   │   │   │   │   ├── config/
│   │   │   │   │   │   ├── ApiConfig.kt            # API настройки (URL, timeouts, logging)
│   │   │   │   │   │   └── AuthConfig.kt           # Авторизация (Basic/Bearer/API Key/OAuth2)
│   │   │   │   │   ├── spec/
│   │   │   │   │   │   ├── RequestSpecBuilder.kt   # Builder request спецификаций
│   │   │   │   │   │   ├── ResponseSpecBuilder.kt  # Builder response спецификаций
│   │   │   │   │   │   └── DefaultSpecs.kt         # Готовые спецификации
│   │   │   │   │   └── client/
│   │   │   │   │       └── ApiClient.kt            # Базовый API клиент
│   │   │   │   └── ui/
│   │   │   │       ├── regression/
│   │   │   │       │   └── ExampleTest.kt
│   │   │   │       └── BaseUiTest.kt
│   │   │   └── selenoid/config/
│   │   │       └── browsers.json
│   │   └── test/
│   │       └── kotlin/
│   │           ├── api/
│   │           │   └── example/
│   │           │       ├── UsersApiClient.kt       # Пример API клиента
│   │           │       └── UsersApiTest.kt         # Пример API теста
│   │           └── ui/...
│   ├── build.gradle.kts
│   └── gradle.properties
├── .gitignore
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
└── settings.gradle.kts
```

---

## API тестирование (REST Assured)

Проект включает готовую инфраструктуру для API тестирования на базе REST Assured.

### Конфигурация API

#### ApiConfig.kt — основные настройки

| Переменная | Тип | Описание | Значение по умолчанию |
|---|---|---|---|
| `API_BASE_URL` | String | Базовый URL API (наследуется из TestConfig) | `http://localhost:8080` |
| `API_BASE_PATH` | String | Базовый путь (например, `/api/v1`) | `` |
| `API_CONNECTION_TIMEOUT` | Int | Таймаут подключения (мс) | `5000` |
| `API_READ_TIMEOUT` | Int | Таймаут чтения ответа (мс) | `10000` |
| `API_MAX_RETRIES` | Int | Максимум повторных попыток | `0` |
| `API_RETRY_DELAY` | Long | Задержка между попытками (мс) | `1000` |
| `API_LOG_REQUEST` | Boolean | Логировать запросы | `true` |
| `API_LOG_RESPONSE` | Boolean | Логировать ответы | `true` |
| `API_LOG_DETAIL` | String | Уровень детализации логов (NONE/HEADERS/BODY/ALL/PARAMS/PATH) | `BODY` |
| `API_CONTENT_TYPE` | String | Content-Type по умолчанию | `application/json` |
| `API_MAX_RESPONSE_TIME` | Long | Максимальное время ответа (мс) | `5000` |

#### AuthConfig.kt — авторизация

| Переменная | Тип | Описание | Значение по умолчанию |
|---|---|---|---|
| `API_AUTH_TYPE` | String | Тип авторизации (NONE/BASIC/BEARER/API_KEY/OAUTH2) | `NONE` |
| `API_AUTH_USERNAME` | String | Логин (для Basic auth) | `` |
| `API_AUTH_PASSWORD` | String | Пароль (для Basic auth) | `` |
| `API_AUTH_TOKEN` | String | Bearer токен | `` |
| `API_AUTH_APIKEY` | String | API ключ | `` |
| `API_AUTH_APIKEY_HEADER` | String | Заголовок для API ключа | `X-API-Key` |
| `API_AUTH_OAUTH2_TOKEN_URL` | String | URL получения OAuth2 токена | `` |
| `API_AUTH_OAUTH2_CLIENT_ID` | String | OAuth2 Client ID | `` |
| `API_AUTH_OAUTH2_CLIENT_SECRET` | String | OAuth2 Client Secret | `` |
| `API_AUTH_OAUTH2_SCOPE` | String | OAuth2 Scope | `` |

### Request Specifications

#### RequestSpecBuilder — создание запросов

| Метод | Описание |
|---|---|
| `buildDefaultSpec()` | Базовая спецификация (JSON, logging) |
| `buildAuthorizedSpec(token?)` | С авторизацией (тип из AuthConfig) |
| `buildMultipartSpec()` | Для загрузки файлов (multipart/form-data) |
| `buildFormUrlEncodedSpec()` | Для form-urlencoded запросов |
| `buildWithHeaders(headers)` | С кастомными headers |
| `buildWithContentType(contentType)` | С кастомным Content-Type |

#### DefaultSpecs — готовые спецификации

```text
DefaultSpecs.defaultRequestSpec          // Базовая
DefaultSpecs.authorizedRequestSpec       // С авторизацией
DefaultSpecs.multipartRequestSpec        // Для файлов
DefaultSpecs.formUrlEncodedRequestSpec   // Для форм
DefaultSpecs.authorizedWithTokenSpec("token") // С кастомным токеном
```

### Response Specifications

#### ResponseSpecBuilder — валидации ответов

| Метод | Описание |
|---|---|
| `buildDefaultSpec()` | Без валидаций |
| `buildSuccessSpec()` | statusCode < 400 |
| `buildStatusSpec(code)` | Конкретный статус-код |
| `buildWithContentTypeSpec()` | Проверка Content-Type |
| `buildWithTimeSpec(maxMs)` | Проверка времени ответа |
| `buildWithJsonSchemaSpec(path)` | Валидация JSON схемы |
| `buildWithFieldSpec(path, matcher)` | Проверка JSON поля |
| `buildErrorSpec(code)` | Для ошибок (4xx/5xx) |
| `buildStrictSpec(code, contentType, maxTime, schema)` | Комплексная валидация |

#### DefaultSpecs — готовые спецификации

```text
DefaultSpecs.defaultResponseSpec         // Без валидаций
DefaultSpecs.successResponseSpec         // statusCode < 400
DefaultSpecs.strictSuccessResponseSpec   // 200 + JSON + time
DefaultSpecs.errorResponseSpec(404)      // Ошибка с кодом
DefaultSpecs.jsonSchemaResponseSpec("schema.json") // Schema validation
DefaultSpecs.timedResponseSpec(3000)     // Response time check
```

### ApiClient — базовый клиент

Все API клиенты наследуются от `ApiClient` и получают готовые методы:

```text
class UsersApiClient : ApiClient() {
    override val basePath: String = "/api/v1"
    override val defaultSpec = DefaultSpecs.authorizedRequestSpec

    fun getUsers(page: Int = 1): Response {
        return get("/users", queryParams = mapOf("page" to page.toString()))
    }

    fun createUser(body: Map<String, Any>): Response {
        return post("/users", body = body)
    }

    fun getUserById(id: String): Response {
        return get("/users/{id}", pathParams = mapOf("id" to id))
    }

    fun updateUser(id: String, body: Map<String, Any>): Response {
        return put("/users/{id}", body = body, pathParams = mapOf("id" to id))
    }

    fun deleteUser(id: String): Response {
        return delete("/users/{id}", pathParams = mapOf("id" to id))
    }
}
```

### Примеры использования

#### Простой тест

```text
@Epic("API Tests")
@Feature("Users")
class UsersApiTest : BaseTest() {

    private val usersClient = UsersApiClient()

    @Test
    @DisplayName("Получить список пользователей")
    fun `get users - success`() {
        val response = usersClient.getUsers()

        response.then()
            .spec(DefaultSpecs.successResponseSpec)
    }
}
```

#### Тест с валидацией тела ответа

```text
@Test
fun `create user - validate response`() {
    val userBody = mapOf(
        "name" to "John Doe",
        "email" to randomEmail()
    )

    val response = usersClient.createUser(userBody)

    response.then()
        .spec(DefaultSpecs.strictSuccessResponseSpec)
        .body("name", equalTo("John Doe"))
        .body("email", containsString("@"))
}
```

#### Тест с JSON Schema валидацией

```text
@Test
fun `get user - validate schema`() {
    val response = usersClient.getUserById("123")

    response.then()
        .spec(DefaultSpecs.successResponseSpec)
        .spec(DefaultSpecs.jsonSchemaResponseSpec("schemas/user.json"))
}
```

#### Тест с проверкой времени ответа

```text
@Test
fun `delete user - check response time`() {
    val response = usersClient.deleteUser("123")

    response.then()
        .spec(DefaultSpecs.successResponseSpec)
        .spec(DefaultSpecs.timedResponseSpec(3000)) // max 3 seconds
}
```

### Переопределение настроек API

```bash
# Через environment variables
API_BASE_URL=http://api.example.com \
API_AUTH_TYPE=BEARER \
API_AUTH_TOKEN=my-token \
API_LOG_DETAIL=ALL \
./gradlew test

# Через JVM properties
./gradlew test \
  -Dapi.base.url=http://api.example.com \
  -Dapi.auth.type=BEARER \
  -Dapi.auth.token=my-token \
  -Dapi.log.detail=ALL

# Через application-*.properties
# application-default.properties
api.base.url=http://api.example.com
api.auth.type=BEARER
api.auth.token=my-token
api.log.detail=BODY
```

---

## Режимы запуска

### 1. **LOCAL** — локальный браузер
Браузер запускается на локальной машине. Требует установленного WebDriver (ChromeDriver, GeckoDriver и т.д.).

```bash
BROWSER_MODE=LOCAL BROWSER_HEADLESS=false ./gradlew test
```

### 2. **SELENOID** — удаленный Selenoid сервер
Браузер запускается в контейнере Selenoid, доступном по сети.

```bash
BROWSER_MODE=SELENOID SELENOID_URL=http://localhost:4444 SELENOID_ENABLE_VNC=true ./gradlew test
```

### 3. **TESTCONTAINERS** — локальные Docker контейнеры
Браузер автоматически запускается в Docker контейнере на локальной машине. Требует Docker.

```bash
BROWSER_MODE=TESTCONTAINERS SELENOID_ENABLE_VNC=true ./gradlew test
```

---

## Как переопределить переменные

### 1. Через Environment variables в IDE (рекомендуется для разработки)

#### IntelliJ IDEA - Run/Debug Configurations

1. Откройте **Run → Edit Configurations...**
2. Выберите вашу конфигурацию Gradle тестов
3. В поле **Environment variables** добавьте переменные через пробел или на новой строке:

```
BROWSER_MODE=TESTCONTAINERS
BROWSER_NAME=chrome
BROWSER_VERSION=latest
BROWSER_TIMEOUT=15000
BROWSER_HEADLESS=true
BROWSER_SIZE=1920x1080
UI_BASE_URL=https://yoUrl.su
SELENOID_ENABLE_VNC=false
```

4. Нажмите **Apply** и **OK**

#### Пример конфигурации для локальной разработки

```
BROWSER_MODE=LOCAL
BROWSER_HEADLESS=true
UI_BASE_URL=http://localhost:8080
```

#### Пример конфигурации для CI/CD

```
BROWSER_MODE=TESTCONTAINERS
BROWSER_HEADLESS=true
BROWSER_TIMEOUT=15000
SELENOID_ENABLE_VNC=false
UI_BASE_URL=http://app:8080
```

---

### 2. Через командную строку

```bash
# Локальный запуск с headless режимом
BROWSER_MODE=LOCAL BROWSER_HEADLESS=true UI_BASE_URL=http://localhost:8080 ./gradlew test

# Запуск в TestContainers
BROWSER_MODE=TESTCONTAINERS BROWSER_NAME=chrome BROWSER_TIMEOUT=15000 ./gradlew test

# Запуск в Selenoid с VNC
BROWSER_MODE=SELENOID SELENOID_URL=http://localhost:4444 SELENOID_ENABLE_VNC=true ./gradlew test

# Запуск с пользовательским URL
UI_BASE_URL=https://yoUrl.su BROWSER_HEADLESS=true ./gradlew test
```

### 3. Через свойства приложения (application-*.properties)

```properties
# application-dev.properties
browser.mode=TESTCONTAINERS
browser.name=chrome
browser.version=latest
browser.timeout=15000
browser.headless=true
browser.size=1920x1080
ui.baseUrl=http://dev.example.com
```

### 4. Через параметры JVM (альтернатива)

```bash
./gradlew test \
  -Dbrowser.mode=TESTCONTAINERS \
  -Dbrowser.name=chrome \
  -Dbrowser.timeout=15000 \
  -Dui.baseUrl=http://test.example.com
```

---

## Примеры конфигураций

### Конфигурация для локальной разработки (headless)

```
BROWSER_MODE=LOCAL
BROWSER_NAME=chrome
BROWSER_HEADLESS=true
BROWSER_TIMEOUT=10000
BROWSER_SIZE=1920x1080
UI_BASE_URL=http://localhost:8080
```

### Конфигурация для CI/CD (TestContainers)

```
BROWSER_MODE=TESTCONTAINERS
BROWSER_NAME=chrome
BROWSER_VERSION=latest
BROWSER_HEADLESS=true
BROWSER_TIMEOUT=15000
SELENOID_ENABLE_VNC=false
UI_BASE_URL=http://app:8080
```

### Конфигурация для удаленного запуска (Selenoid)

```
BROWSER_MODE=SELENOID
BROWSER_NAME=chrome
BROWSER_VERSION=latest
SELENOID_URL=http://selenoid-server:4444
SELENOID_ENABLE_VNC=true
BROWSER_TIMEOUT=20000
UI_BASE_URL=http://example.com
```

### Конфигурация для тестирования на Яндекс

```
BROWSER_MODE=LOCAL
BROWSER_HEADLESS=false
UI_BASE_URL=https://yoUrl.su
BROWSER_TIMEOUT=15000
```

---

## Возможности логирования

- **SLF4J логирование** — все операции браузера логируются в консоль
- **Allure отчеты** — скриншоты и логи автоматически прикрепляются к отчетам
- **TestContainers логирование** — вывод контейнера доступен в логах
- **VNC доступ** — при включении можно смотреть сессию браузера в реальном времени

---

## Требования

- **Java 17+**
- **Gradle 7.0+**
- **Docker** (для TESTCONTAINERS и SELENOID режимов)
- **WebDriver** (для LOCAL режима)
- **REST Assured 5.5.0** (для API тестов — уже включён в зависимости)

---

## Команды для запуска

```bash
# UI тесты
./gradlew test

# Запуск в TestContainers с переменными окружения
BROWSER_MODE=TESTCONTAINERS BROWSER_HEADLESS=true ./gradlew test

# Запуск в Selenoid
BROWSER_MODE=SELENOID SELENOID_URL=http://localhost:4444 ./gradlew test

# Запуск с пользовательским URL
UI_BASE_URL=https://yoUrl.su ./gradlew test

# API тесты (с кастомной конфигурацией)
API_BASE_URL=http://api.example.com \
API_AUTH_TYPE=BEARER \
API_AUTH_TOKEN=my-token \
./gradlew test

# Генерация Allure отчета
./gradlew allureServe
```

---

## Поддерживаемые браузеры

- **Chrome** — версии от 90 и выше
- **Firefox** — версии от 88 и выше
- **Edge** — версии от 90 и выше

---

## Лицензия

MIT License
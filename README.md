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
│   │   │   │   └── ui/
│   │   │   │       ├── regression/
│   │   │   │       │   └── ExampleTest.kt
│   │   │   │       └── BaseUiTest.kt
│   │   │   └── selenoid/config/
│   │   │       └── browsers.json
│   │   └── test/
│   │       └── kotlin/ui/...
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

---

## Команды для запуска

```bash
# Локальный запуск (headless)
./gradlew test

# Запуск в TestContainers с переменными окружения
BROWSER_MODE=TESTCONTAINERS BROWSER_HEADLESS=true ./gradlew test

# Запуск в Selenoid
BROWSER_MODE=SELENOID SELENOID_URL=http://localhost:4444 ./gradlew test

# Запуск с пользовательским URL
UI_BASE_URL=https://yoUrl.su ./gradlew test

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
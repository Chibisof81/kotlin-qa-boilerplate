package core

import core.core.listeners.RetryListener
import core.core.listeners.TestLifecycleLogger
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Базовый класс для API-тестов.
 *
 * БЕЗ Selenide/AllureAttachmentListener — только логирование и retry.
 * API-тесты не используют браузер, поэтому скриншоты и логи браузера не нужны.
 */
@ExtendWith(
    TestLifecycleLogger::class,
    RetryListener::class
)
abstract class BaseApiTest : BaseTest()

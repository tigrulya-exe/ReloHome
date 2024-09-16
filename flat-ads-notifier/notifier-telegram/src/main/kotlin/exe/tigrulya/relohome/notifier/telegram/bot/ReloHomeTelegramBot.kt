package exe.tigrulya.relohome.notifier.telegram.bot

import dev.inmo.micro_utils.coroutines.subscribeSafely
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.bot.settings.limiters.CommonLimiter
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import exe.tigrulya.relohome.api.user_handler.UserHandlerGateway
import exe.tigrulya.relohome.localization.Localization
import exe.tigrulya.relohome.notifier.telegram.bot.handlers.*
import exe.tigrulya.relohome.notifier.telegram.bot.state.DefaultLocalizationManager
import exe.tigrulya.relohome.notifier.telegram.bot.state.DefaultUserStatesManager
import exe.tigrulya.relohome.notifier.telegram.bot.state.repo.InMemoryUserLocalesRepository
import exe.tigrulya.relohome.notifier.telegram.bot.state.repo.InMemoryUserStatesRepository
import exe.tigrulya.relohome.notifier.telegram.bot.state.repo.UserLocalesRepository
import exe.tigrulya.relohome.notifier.telegram.bot.state.repo.UserStatesRepository
import exe.tigrulya.relohome.notifier.telegram.serde.JsonSearchOptionsDeserializer
import exe.tigrulya.relohome.notifier.telegram.serde.SearchOptionsDeserializer
import exe.tigrulya.relohome.util.LoggerProperty
import io.ktor.util.logging.*
import kotlinx.coroutines.Job

class ReloHomeTelegramBot(
    botToken: String,
    userHandlerGateway: UserHandlerGateway,
    handlerWebUrl: String,
    searchOptionsDeserializer: SearchOptionsDeserializer = JsonSearchOptionsDeserializer(),
    userStatesRepository: UserStatesRepository = InMemoryUserStatesRepository(),
    userLocalesRepo: UserLocalesRepository = InMemoryUserLocalesRepository(),
    localesDirPath: String = "locales/tg-notifier",
    requestsPerSecond: Int = 10
) : AutoCloseable {
    private var pollingJob: Job? = null

    private val logger by LoggerProperty()

    val keyboardFactory: KeyboardFactory = KeyboardFactory(
        Localization(localesDirPath),
        handlerWebUrl
    )

    val tgBot: TelegramBot = telegramBot(botToken) {
        // todo mb add adapter to guava coroutine ratelimiter
        requestsLimiter = CommonLimiter(
            lockCount = requestsPerSecond,
            regenTime = 1000L
        )
    }

    private val ctx = ReloHomeContext(
        userHandlerGateway,
        DefaultUserStatesManager(userStatesRepository),
        keyboardFactory,
        searchOptionsDeserializer,
        DefaultLocalizationManager(
            keyboardFactory.localization,
            userLocalesRepo = userLocalesRepo
        ),
    )

    suspend fun start() {
        pollingJob = tgBot.buildBehaviourWithLongPolling(
            defaultExceptionsHandler = { logger.error(it) }
        ) {
            handleStartCommand(ctx)

            handleSetLocale(ctx)

            localeChosenHandler(ctx)

            handleSearchOptions(ctx)

            handleEnableSearch(ctx)

            handleShowStatistics(ctx)

            handleShowSubscriptionInfo(ctx)

            allUpdatesFlow.subscribeSafely(this) { println(it) }
        }
    }

    override fun close() {
        tgBot.close()
        pollingJob?.cancel()
    }
}
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
import exe.tigrulya.relohome.notifier.telegram.bot.state.repo.InMemoryUserStatesRepository
import exe.tigrulya.relohome.notifier.telegram.bot.state.repo.UserStatesRepository
import exe.tigrulya.relohome.notifier.telegram.serde.JsonSearchOptionsDeserializer
import exe.tigrulya.relohome.notifier.telegram.serde.SearchOptionsDeserializer
import kotlinx.coroutines.Job

class ReloHomeTelegramBot(
    botToken: String,
    userHandlerGateway: UserHandlerGateway,
    handlerWebUrl: String,
    searchOptionsDeserializer: SearchOptionsDeserializer = JsonSearchOptionsDeserializer(),
    userStatesRepository: UserStatesRepository = InMemoryUserStatesRepository(),
    localesDirPath: String = "locales/tg-notifier",
    requestsPerSecond: Int = 10
) : AutoCloseable {
    private var pollingJob: Job? = null

    val mainKeyboardProvider: MainKeyboardProvider = MainKeyboardProvider(handlerWebUrl)

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
        mainKeyboardProvider,
        searchOptionsDeserializer,
        DefaultLocalizationManager(
            Localization(localesDirPath)
        ),
    )

    suspend fun start() {
        pollingJob = tgBot.buildBehaviourWithLongPolling {
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
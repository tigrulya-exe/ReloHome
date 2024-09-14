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
    val userHandlerGateway: UserHandlerGateway,
    val handlerWebUrl: String,
    val searchOptionsDeserializer: SearchOptionsDeserializer = JsonSearchOptionsDeserializer(),
    val userStatesRepository: UserStatesRepository = InMemoryUserStatesRepository(),
    val localesDirPath: String = "locales/tg-notifier",
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

    private lateinit var ctx: ReloHomeContext

    suspend fun start() {
        pollingJob = tgBot.buildBehaviourWithLongPolling {
            ctx = ReloHomeContext(
                userHandlerGateway,
                DefaultUserStatesManager(userStatesRepository),
                mainKeyboardProvider,
                searchOptionsDeserializer,
                DefaultLocalizationManager(
                    Localization(localesDirPath)
                ),
                this
            )

            with(ctx) {
                handleStartCommand()

                handleSetLocale()

                localeChosenHandler()

                handleSearchOptions()

                handleEnableSearch()

                handleShowStatistics()

                handleShowSubscriptionInfo()
            }

            allUpdatesFlow.subscribeSafely(this) { println(it) }
        }
    }

    override fun close() {
        tgBot.close()
        pollingJob?.cancel()
    }
}
package exe.tigrulya.relohome.notifier.telegram.bot

import dev.inmo.micro_utils.coroutines.subscribeSafely
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.bot.settings.limiters.CommonLimiter
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import exe.tigrulya.relohome.api.user_handler.UserHandlerGateway
import exe.tigrulya.relohome.notifier.telegram.bot.handlers.*
import exe.tigrulya.relohome.notifier.telegram.serde.JsonSearchOptionsDeserializer
import exe.tigrulya.relohome.notifier.telegram.serde.SearchOptionsDeserializer
import kotlinx.coroutines.Job

class ReloHomeTelegramBot(
    botToken: String,
    val userHandlerGateway: UserHandlerGateway,
    handlerWebUrl: String,
    val searchOptionsDeserializer: SearchOptionsDeserializer = JsonSearchOptionsDeserializer(),
    requestsPerSecond: Int = 10
) : AutoCloseable {
    private var pollingJob: Job? = null

    private val mainKeyboardProvider = MainKeyboardProvider(handlerWebUrl)

    val tgBot: TelegramBot = telegramBot(botToken) {
        // todo mb add adapter to guava coroutine ratelimiter
        requestsLimiter = CommonLimiter(
            lockCount = requestsPerSecond,
            regenTime = 1000L
        )
    }

    suspend fun start() {
        pollingJob = tgBot.buildBehaviourWithLongPolling {
            handleStartCommand(userHandlerGateway, mainKeyboardProvider)

            handleSearchOptions(userHandlerGateway, searchOptionsDeserializer)

            handleEnableSearch(userHandlerGateway, mainKeyboardProvider)

            handleShowStatistics(userHandlerGateway)

            handleShowSubscriptionInfo(userHandlerGateway)

            allUpdatesFlow.subscribeSafely(this) { println(it) }
        }
    }

    override fun close() {
        tgBot.close()
        pollingJob?.cancel()
    }
}
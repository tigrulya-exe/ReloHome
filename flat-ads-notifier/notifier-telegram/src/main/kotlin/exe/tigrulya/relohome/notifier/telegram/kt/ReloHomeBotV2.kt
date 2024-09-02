package exe.tigrulya.relohome.notifier.telegram.kt

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.logging.LogLevel
import exe.tigrulya.relohome.api.user_handler.UserHandlerGateway
import exe.tigrulya.relohome.notifier.telegram.kt.handlers.*
import exe.tigrulya.relohome.notifier.telegram.serde.JsonSearchOptionsDeserializer
import exe.tigrulya.relohome.notifier.telegram.serde.SearchOptionsDeserializer
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

// todo add relohome execution environment
class ReloHomeBotV2(
    botToken: String,
    userHandlerGateway: UserHandlerGateway,
    handlerWebUrl: String,
    searchOptionsDeserializer: SearchOptionsDeserializer = JsonSearchOptionsDeserializer(),
    requestsPerSecond: Int = 10,
    botLogLevel: LogLevel = LogLevel.None,
) {
    private val mainKeyboardProvider: MainKeyboardProvider = MainKeyboardProvider(handlerWebUrl)

    private val bot = bot {

        token = botToken
        logLevel = botLogLevel
        // TODO
        coroutineDispatcher = Executors.newFixedThreadPool(8).asCoroutineDispatcher()

        dispatch {
            startCommand(userHandlerGateway, mainKeyboardProvider)

            enableBotReply(userHandlerGateway, mainKeyboardProvider)

            statisticsReply(mainKeyboardProvider)

            subscriptionInfoReply(mainKeyboardProvider)

            searchOptionsSetReply(
                userHandlerGateway,
                searchOptionsDeserializer,
                mainKeyboardProvider
            )
        }
    }

    fun start() = bot.startPolling()

    fun stop() = bot.stopPolling()
}
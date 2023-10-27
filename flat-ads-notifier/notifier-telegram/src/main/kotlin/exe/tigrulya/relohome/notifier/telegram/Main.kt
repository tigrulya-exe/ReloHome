package exe.tigrulya.relohome.notifier.telegram

import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

private const val MY_ID: Long = 479226955
private const val BOT_USERNAME = "NOPE"
private const val BOT_TOKEN = "NOPE"

fun main(args: Array<String>) {
    val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
    botsApi.registerBot(
            EnhancedWeatherNsuBot(
                BOT_TOKEN,
                BOT_USERNAME,
                MY_ID
            )
    )

}
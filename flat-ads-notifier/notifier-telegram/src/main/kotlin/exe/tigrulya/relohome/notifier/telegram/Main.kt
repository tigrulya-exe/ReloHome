package exe.tigrulya.relohome.notifier.telegram

import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

private const val MY_ID: Long = 479226955
private const val BOT_USERNAME = "relo_home_bot"
private const val BOT_TOKEN = "NOPE"

fun main() {
    val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
    val reloHomeBot = ReloHomeBot(
        BOT_TOKEN,
        BOT_USERNAME,
        MY_ID
    )
    botsApi.registerBot(reloHomeBot)
}
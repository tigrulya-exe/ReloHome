package exe.tigrulya.relohome.notifier.telegram.kt.handlers

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import exe.tigrulya.relohome.notifier.telegram.kt.MainKeyboardProvider
import exe.tigrulya.relohome.notifier.telegram.kt.env.reply
import exe.tigrulya.relohome.notifier.telegram.kt.env.senderId
import exe.tigrulya.relohome.notifier.telegram.kt.messageStartingWithPrefix

fun Dispatcher.statisticsReply(
    keyboardProvider: MainKeyboardProvider
) = messageStartingWithPrefix(
    MainKeyboardProvider.STATISTICS_BUTTON_TEXT
) {
    reply(
        message = "Collecting enough data to build statistics, come back later",
        replyMarkup = keyboardProvider.get(senderId)
    )
}

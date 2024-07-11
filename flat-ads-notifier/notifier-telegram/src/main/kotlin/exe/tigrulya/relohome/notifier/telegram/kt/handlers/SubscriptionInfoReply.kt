package exe.tigrulya.relohome.notifier.telegram.kt.handlers

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import exe.tigrulya.relohome.notifier.telegram.kt.MainKeyboardProvider
import exe.tigrulya.relohome.notifier.telegram.kt.env.reply
import exe.tigrulya.relohome.notifier.telegram.kt.env.senderId
import exe.tigrulya.relohome.notifier.telegram.kt.messageStartingWithPrefix

fun Dispatcher.subscriptionInfoReply(
    keyboardProvider: MainKeyboardProvider
) = messageStartingWithPrefix(
    MainKeyboardProvider.SUBSCRIPTION_INFO_BUTTON_TEXT
) {
    reply(
        message = "Bot is free at the moment, but it will change soon :)",
        replyMarkup = keyboardProvider.get(senderId)
    )
}

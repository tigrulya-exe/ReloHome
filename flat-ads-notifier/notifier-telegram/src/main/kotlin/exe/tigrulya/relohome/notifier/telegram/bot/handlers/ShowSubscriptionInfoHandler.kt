package exe.tigrulya.relohome.notifier.telegram.bot.handlers

import dev.inmo.tgbotapi.extensions.api.send.send
import exe.tigrulya.relohome.notifier.telegram.bot.MainKeyboardProvider
import exe.tigrulya.relohome.notifier.telegram.bot.ReloHomeContext
import exe.tigrulya.relohome.notifier.telegram.bot.ext.onTextStartingWith
import exe.tigrulya.relohome.notifier.telegram.bot.ext.senderId

suspend fun ReloHomeContext.handleShowSubscriptionInfo() = onTextStartingWith(
    MainKeyboardProvider.SUBSCRIPTION_INFO_BUTTON_TEXT,
) { message ->
    send(
        chatId = message.senderId(),
        text = "Bot is free at the moment, but it will change soon :)"
    )
}
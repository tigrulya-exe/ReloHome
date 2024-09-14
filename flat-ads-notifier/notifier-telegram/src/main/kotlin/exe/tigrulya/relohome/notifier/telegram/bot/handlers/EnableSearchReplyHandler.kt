package exe.tigrulya.relohome.notifier.telegram.bot.handlers

import dev.inmo.tgbotapi.extensions.api.send.send
import exe.tigrulya.relohome.notifier.telegram.bot.MainKeyboardProvider
import exe.tigrulya.relohome.notifier.telegram.bot.ReloHomeContext
import exe.tigrulya.relohome.notifier.telegram.bot.ext.onTextStartingWith
import exe.tigrulya.relohome.notifier.telegram.bot.ext.sender
import exe.tigrulya.relohome.notifier.telegram.bot.ext.senderId
import exe.tigrulya.relohome.notifier.telegram.bot.ext.withSimpleErrorHandling

suspend fun ReloHomeContext.handleEnableSearch() = onTextStartingWith(
    MainKeyboardProvider.ENABLED_BOT_BUTTON_TEXT,
    MainKeyboardProvider.DISABLED_BOT_BUTTON_TEXT
) { message ->

    val searchEnabled = withSimpleErrorHandling(message, "error enabling search") {
        userHandlerGateway.toggleSearch(message.sender())
    }

    send(
        chatId = message.senderId(),
        text = "The flat ad search is ${if (searchEnabled) "enabled" else "disabled"}",
        replyMarkup = keyboardProvider.get(message.sender(), searchEnabled)
    )
}
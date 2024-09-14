package exe.tigrulya.relohome.notifier.telegram.bot.handlers

import dev.inmo.tgbotapi.extensions.api.send.send
import exe.tigrulya.relohome.notifier.telegram.bot.MainKeyboardProvider
import exe.tigrulya.relohome.notifier.telegram.bot.ReloHomeContext
import exe.tigrulya.relohome.notifier.telegram.bot.ext.onTextStartingWith
import exe.tigrulya.relohome.notifier.telegram.bot.ext.sender
import exe.tigrulya.relohome.notifier.telegram.bot.ext.senderId

suspend fun ReloHomeContext.handleShowStatistics() = onTextStartingWith(
    MainKeyboardProvider.STATISTICS_BUTTON_TEXT,
) { message ->
    withLocalization(message.sender()) {
        send(
            chatId = message.senderId(),
            text = constant("handlers.statistics.info"),
        )
    }
}
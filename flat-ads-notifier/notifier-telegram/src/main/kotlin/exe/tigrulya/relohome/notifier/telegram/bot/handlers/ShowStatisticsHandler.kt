package exe.tigrulya.relohome.notifier.telegram.bot.handlers

import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import exe.tigrulya.relohome.api.user_handler.UserHandlerGateway
import exe.tigrulya.relohome.notifier.telegram.bot.MainKeyboardProvider
import exe.tigrulya.relohome.notifier.telegram.bot.ext.onTextStartingWith
import exe.tigrulya.relohome.notifier.telegram.bot.ext.senderId

suspend fun BehaviourContext.handleShowStatistics(
    userHandlerGateway: UserHandlerGateway
) = onTextStartingWith(
    MainKeyboardProvider.STATISTICS_BUTTON_TEXT,
) { message ->
    send(
        chatId = message.senderId(),
        text = "Collecting enough data to build statistics, come back later",
    )
}
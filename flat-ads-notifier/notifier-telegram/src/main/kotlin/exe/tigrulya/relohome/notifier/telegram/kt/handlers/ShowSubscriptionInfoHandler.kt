package exe.tigrulya.relohome.notifier.telegram.kt.handlers

import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import exe.tigrulya.relohome.api.user_handler.UserHandlerGateway
import exe.tigrulya.relohome.notifier.telegram.kt.MainKeyboardProvider
import exe.tigrulya.relohome.notifier.telegram.kt.ext.onTextStartingWith
import exe.tigrulya.relohome.notifier.telegram.kt.ext.senderId

suspend fun BehaviourContext.handleShowSubscriptionInfo(
    userHandlerGateway: UserHandlerGateway
) = onTextStartingWith(
    MainKeyboardProvider.SUBSCRIPTION_INFO_BUTTON_TEXT,
) { message ->
    send(
        chatId = message.senderId(),
        text = "Bot is free at the moment, but it will change soon :)"
    )
}
package exe.tigrulya.relohome.notifier.telegram.kt.handlers

import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import exe.tigrulya.relohome.api.user_handler.UserHandlerGateway
import exe.tigrulya.relohome.notifier.telegram.kt.MainKeyboardProvider
import exe.tigrulya.relohome.notifier.telegram.kt.ext.onTextStartingWith
import exe.tigrulya.relohome.notifier.telegram.kt.ext.sender
import exe.tigrulya.relohome.notifier.telegram.kt.ext.senderId
import exe.tigrulya.relohome.notifier.telegram.kt.ext.withSimpleErrorHandling

suspend fun BehaviourContext.handleEnableSearch(
    userHandlerGateway: UserHandlerGateway,
    keyboardProvider: MainKeyboardProvider
) = onTextStartingWith(
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
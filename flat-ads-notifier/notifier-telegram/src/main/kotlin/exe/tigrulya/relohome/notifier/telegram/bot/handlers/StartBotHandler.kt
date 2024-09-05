package exe.tigrulya.relohome.notifier.telegram.bot.handlers

import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.fromUserOrThrow
import dev.inmo.tgbotapi.types.message.HTMLParseMode
import exe.tigrulya.relohome.api.user_handler.UserHandlerGateway
import exe.tigrulya.relohome.model.City
import exe.tigrulya.relohome.model.UserCreateDto
import exe.tigrulya.relohome.notifier.telegram.bot.MainKeyboardProvider
import exe.tigrulya.relohome.notifier.telegram.bot.ext.onCommandWithErrorHandling
import exe.tigrulya.relohome.notifier.telegram.bot.ext.sender
import exe.tigrulya.relohome.notifier.telegram.bot.ext.senderId
import exe.tigrulya.relohome.notifier.telegram.bot.ext.withSimpleErrorHandling
import exe.tigrulya.relohome.notifier.telegram.util.asCode

suspend fun BehaviourContext.handleStartCommand(
    userHandlerGateway: UserHandlerGateway,
    keyboardProvider: MainKeyboardProvider
) = onCommandWithErrorHandling("start") { message ->
    val user = withSimpleErrorHandling(message, "Bot currently works only in private chats") {
        message.fromUserOrThrow().user
    }

    withSimpleErrorHandling(message, "Error registering user") {
        userHandlerGateway.registerUser(
            UserCreateDto(
                name = user.username?.withoutAt
                    ?: throw IllegalStateException("Please at first set your id in Telegram settings"),
                externalId = message.sender()
            )
        )
    }

    withSimpleErrorHandling(message, "Error setting location") {
        userHandlerGateway.setLocation(
            message.sender(),
            City(
                name = "Tbilisi",
                country = "Georgia"
            )
        )
    }

    send(
        chatId = message.senderId(),
        text = "Hello there! Welcome to our apartment hunting bot! " +
                "We've designed this bot to keep an eye on all the major real estate listing sites for you. " +
                "Once we spot an apartment that matches your preferences, we'll shoot you a notification right away. \n\n" +
                "To make sure you receive only relevant ads, just click on the \n " +
                "${MainKeyboardProvider.OPTIONS_BUTTON_TEXT.asCode()} button below to set your preferences.",
        replyMarkup = keyboardProvider.get(message.sender(), searchEnabled = true),
        parseMode = HTMLParseMode
    )
}
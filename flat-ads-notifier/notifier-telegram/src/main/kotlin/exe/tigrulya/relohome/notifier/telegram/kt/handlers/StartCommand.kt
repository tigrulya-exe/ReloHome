package exe.tigrulya.relohome.notifier.telegram.kt.handlers

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import exe.tigrulya.relohome.api.user_handler.UserHandlerGateway
import exe.tigrulya.relohome.model.City
import exe.tigrulya.relohome.model.UserCreateDto
import exe.tigrulya.relohome.notifier.telegram.bot.ReloHomeBot
import exe.tigrulya.relohome.notifier.telegram.kt.*
import exe.tigrulya.relohome.notifier.telegram.kt.env.*
import exe.tigrulya.relohome.notifier.telegram.util.asCode

fun Dispatcher.startCommand(
    userHandlerGateway: UserHandlerGateway,
    keyboardProvider: MainKeyboardProvider
) = command("start") {

    enableErrorHandling {
        withSimpleErrorHandling("Error registering user") {
            userHandlerGateway.registerUser(
                UserCreateDto(
                    name = senderUsername,
                    externalId = senderId
                )
            )
        }

        withSimpleErrorHandling("Error setting location") {
            userHandlerGateway.setLocation(
                senderId,
                City(
                    name = "Tbilisi",
                    country = "Georgia"
                )
            )
        }

        reply(
            "Hello there! Welcome to our apartment hunting bot! " +
                    "We've designed this bot to keep an eye on all the major real estate listing sites for you. " +
                    "Once we spot an apartment that matches your preferences, we'll shoot you a notification right away. \n\n" +
                    "To make sure you receive only relevant ads, just click on the \n " +
                    "${ReloHomeBot.OPTIONS_BUTTON_TEXT.asCode()} button below to set your preferences.",
            replyMarkup = keyboardProvider.get(senderId)
        )
    }
}


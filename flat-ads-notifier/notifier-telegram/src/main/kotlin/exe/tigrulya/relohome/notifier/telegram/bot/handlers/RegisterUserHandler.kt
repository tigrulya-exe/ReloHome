package exe.tigrulya.relohome.notifier.telegram.bot.handlers

import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.types.chat.User
import dev.inmo.tgbotapi.types.message.HTMLParseMode
import exe.tigrulya.relohome.model.City
import exe.tigrulya.relohome.model.UserCreateDto
import exe.tigrulya.relohome.notifier.telegram.bot.MainKeyboardProvider
import exe.tigrulya.relohome.notifier.telegram.bot.ReloHomeContext
import exe.tigrulya.relohome.notifier.telegram.bot.ext.externalId
import exe.tigrulya.relohome.notifier.telegram.bot.ext.withSimpleErrorHandling
import exe.tigrulya.relohome.notifier.telegram.util.asCode


suspend fun ReloHomeContext.registerUser(
    user: User,
    locale: String
) {
    withSimpleErrorHandling(user.id, "Error registering user") {
        userHandlerGateway.registerUser(
            UserCreateDto(
                name = user.username?.withoutAt
                    ?: throw IllegalStateException("Please at first set your id in Telegram settings"),
                externalId = user.externalId(),
                locale = locale
            )
        )
    }

    withSimpleErrorHandling(user.id, "Error setting location") {
        userHandlerGateway.setLocation(
            user.externalId(),
            City(
                name = "Tbilisi",
                country = "Georgia"
            )
        )
    }

    send(
        chatId = user.id,
        text = "Hello there! Welcome to our apartment hunting bot! " +
                "We've designed this bot to keep an eye on all the major real estate listing sites for you. " +
                "Once we spot an apartment that matches your preferences, we'll shoot you a notification right away. \n\n" +
                "To make sure you receive only relevant ads, just click on the \n " +
                "${MainKeyboardProvider.OPTIONS_BUTTON_TEXT.asCode()} button below to set your preferences.",
        replyMarkup = keyboardProvider.get(user.externalId(), searchEnabled = true),
        parseMode = HTMLParseMode
    )
}

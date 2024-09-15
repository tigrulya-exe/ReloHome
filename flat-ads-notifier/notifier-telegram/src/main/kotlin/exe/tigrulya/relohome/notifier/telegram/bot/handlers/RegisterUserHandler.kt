package exe.tigrulya.relohome.notifier.telegram.bot.handlers

import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.chat.User
import dev.inmo.tgbotapi.types.message.HTMLParseMode
import exe.tigrulya.relohome.model.City
import exe.tigrulya.relohome.model.UserCreateDto
import exe.tigrulya.relohome.notifier.telegram.bot.ReloHomeContext
import exe.tigrulya.relohome.notifier.telegram.bot.ext.externalId
import exe.tigrulya.relohome.notifier.telegram.bot.ext.withSimpleErrorHandling


suspend fun BehaviourContext.registerUser(
    user: User,
    locale: String,
    ctx: ReloHomeContext
) {
    with(ctx) {
        withLocalization(user.externalId()) {
            withSimpleErrorHandling(user.id, constant("handlers.registration.create-user.error")) {
                userHandlerGateway.registerUser(
                    UserCreateDto(
                        name = user.username?.withoutAt
                            ?: throw IllegalStateException(constant("handlers.registration.create-user.no-id")),
                        externalId = user.externalId(),
                        locale = locale
                    )
                )
            }

            withSimpleErrorHandling(user.id, constant("handlers.registration.set-location.error")) {
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
                text = constant("handlers.registration.success"),
                replyMarkup = keyboardProvider.get(user.externalId(), searchEnabled = true),
                parseMode = HTMLParseMode
            )
        }
    }
}

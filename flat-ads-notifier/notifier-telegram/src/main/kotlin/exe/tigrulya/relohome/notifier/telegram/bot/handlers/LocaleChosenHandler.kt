package exe.tigrulya.relohome.notifier.telegram.bot.handlers

import dev.inmo.tgbotapi.extensions.api.answers.answerCallbackQuery
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.queries.callback.MessageDataCallbackQuery
import exe.tigrulya.relohome.notifier.telegram.bot.ReloHomeContext
import exe.tigrulya.relohome.notifier.telegram.bot.ext.onCallbackData
import exe.tigrulya.relohome.notifier.telegram.bot.ext.sender
import exe.tigrulya.relohome.notifier.telegram.bot.ext.senderId
import exe.tigrulya.relohome.notifier.telegram.bot.ext.withSimpleErrorHandling
import exe.tigrulya.relohome.notifier.telegram.bot.state.repo.UserState

const val LOCALE_VALUE_CALLBACK_DATA_PREFIX = "set_locale_"

suspend fun BehaviourContext.localeChosenHandler(ctx: ReloHomeContext) {
    localeChosenDefaultHandler(ctx)

    localeChosenAtFirstTimeHandler(ctx)
}

suspend fun BehaviourContext.localeChosenDefaultHandler(ctx: ReloHomeContext) = onCallbackData(
    filter = { it.startsWith(LOCALE_VALUE_CALLBACK_DATA_PREFIX) }
) { message ->
    with(ctx) {
        onlyOnState(message.sender(), UserState.SEARCH_OPTIONS_PROVIDED) {
            withSimpleErrorHandling(message.senderId(), constant(message.sender(), "handlers.locale-chosen.error")) {
                userHandlerGateway.setLocale(message.sender(), message.locale)
            }

            answerCallbackQuery(message.id)

            localization.setLocale(message.sender(), message.locale)

            send(message.from, constant(message.sender(), "handlers.locale-chosen.error"))
        }
    }
}

suspend fun BehaviourContext.localeChosenAtFirstTimeHandler(ctx: ReloHomeContext) = onCallbackData(
    filter = { it.startsWith(LOCALE_VALUE_CALLBACK_DATA_PREFIX) }
) { message ->
    with(ctx) {
        onlyOnState(message.sender(), UserState.NEW) {

            registerUser(
                user = message.from,
                locale = message.locale,
                ctx = ctx
            )

            answerCallbackQuery(message.id)

            transition(message.sender(), UserState.LOCALE_PROVIDED)
        }
    }
}

val MessageDataCallbackQuery.locale
    get() = data.removePrefix(LOCALE_VALUE_CALLBACK_DATA_PREFIX)

package exe.tigrulya.relohome.notifier.telegram.bot.handlers

import dev.inmo.tgbotapi.extensions.api.answers.answerCallbackQuery
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.types.queries.callback.MessageDataCallbackQuery
import exe.tigrulya.relohome.notifier.telegram.bot.ReloHomeContext
import exe.tigrulya.relohome.notifier.telegram.bot.ext.onCallbackData
import exe.tigrulya.relohome.notifier.telegram.bot.ext.sender
import exe.tigrulya.relohome.notifier.telegram.bot.ext.senderId
import exe.tigrulya.relohome.notifier.telegram.bot.ext.withSimpleErrorHandling
import exe.tigrulya.relohome.notifier.telegram.bot.state.UserState

const val LOCALE_VALUE_CALLBACK_DATA_PREFIX = "set_locale_"

suspend fun ReloHomeContext.localeChosenHandler() {
    localeChosenDefaultHandler()

    localeChosenAtFirstTimeHandler()
}

suspend fun ReloHomeContext.localeChosenDefaultHandler() = onCallbackData(
    filter = { it.startsWith(LOCALE_VALUE_CALLBACK_DATA_PREFIX) }
) { message ->
    userStatesManager.onlyOnState(message.sender(), UserState.SEARCH_OPTIONS_PROVIDED) {
        send(message.from, "CanChangeLocaleState")

        withSimpleErrorHandling(message.senderId(), "Error changing language") {
            userHandlerGateway.setLocale(message.sender(), message.locale)
        }

        answerCallbackQuery(message.id)

        send(message.from, "Language changed successfully!")
    }
}

suspend fun ReloHomeContext.localeChosenAtFirstTimeHandler() = onCallbackData(
    filter = { it.startsWith(LOCALE_VALUE_CALLBACK_DATA_PREFIX) }
) { message ->
    userStatesManager.onlyOnState(message.sender(), UserState.NEW) {
        send(message.from, "LocaleAskedState")

        registerUser(
            user = message.from,
            locale = message.locale,
        )

        answerCallbackQuery(message.id)

        transition(message.sender(), UserState.LOCALE_PROVIDED)
    }
}

val MessageDataCallbackQuery.locale
    get() = data.removePrefix(LOCALE_VALUE_CALLBACK_DATA_PREFIX)

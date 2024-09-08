package exe.tigrulya.relohome.notifier.telegram.bot.handlers

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import exe.tigrulya.relohome.api.user_handler.UserHandlerGateway
import exe.tigrulya.relohome.notifier.telegram.bot.MainKeyboardProvider
import exe.tigrulya.relohome.notifier.telegram.bot.ext.onCallbackData

const val LOCALE_VALUE_CALLBACK_DATA_PREFIX = "set_locale_"

suspend fun BehaviourContext.localeChosenHandler(
    userHandlerGateway: UserHandlerGateway,
    keyboardProvider: MainKeyboardProvider
) = onCallbackData(
    filter = { it.startsWith(LOCALE_VALUE_CALLBACK_DATA_PREFIX) }
) { message ->
    registerUser(
        user = message.from,
        locale = message.data.removePrefix(LOCALE_VALUE_CALLBACK_DATA_PREFIX),
        userHandlerGateway,
        keyboardProvider
    )
}

// todo do it with state machine
//suspend fun BehaviourContext.setLocaleHandler(
//    userHandlerGateway: UserHandlerGateway,
//    keyboardProvider: MainKeyboardProvider
//) = onTextStartingWith(
//    CHANGE_LOCALE_BUTTON_TEXT
//) { message ->
//    val locale = message.data.removePrefix(LOCALE_VALUE_CALLBACK_DATA_PREFIX)
//
//    withSimpleErrorHandling(message.from.id, "Error changing language") {
//        userHandlerGateway.setLocale(message.message.sender(), locale)
//    }
//}

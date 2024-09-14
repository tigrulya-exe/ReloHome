package exe.tigrulya.relohome.notifier.telegram.bot.handlers

import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.utils.types.buttons.dataButton
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.utils.row
import exe.tigrulya.relohome.notifier.telegram.bot.MainKeyboardProvider.Companion.CHANGE_LOCALE_BUTTON_TEXT
import exe.tigrulya.relohome.notifier.telegram.bot.ReloHomeContext
import exe.tigrulya.relohome.notifier.telegram.bot.ext.onTextStartingWith
import exe.tigrulya.relohome.notifier.telegram.bot.ext.sender
import exe.tigrulya.relohome.notifier.telegram.bot.ext.senderId

suspend fun ReloHomeContext.handleSetLocale() =
    onTextStartingWith(CHANGE_LOCALE_BUTTON_TEXT) { message ->
        withLocalization(message.sender()) {
            send(
                chatId = message.senderId(),
                text = constant("handlers.set-locale.question-message"),
                replyMarkup = setLocaleKeyboard()
            )
        }
    }

// TODO get supported locales from server
fun setLocaleKeyboard(): InlineKeyboardMarkup = inlineKeyboard {
    row {
        dataButton("\uD83C\uDDF7\uD83C\uDDFA Русский", LOCALE_VALUE_CALLBACK_DATA_PREFIX + "ru")
    }
    row {
        dataButton("\uD83C\uDDEC\uD83C\uDDE7 English", LOCALE_VALUE_CALLBACK_DATA_PREFIX + "en")
    }
}
package exe.tigrulya.relohome.notifier.telegram.bot.handlers

import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.types.buttons.dataButton
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.utils.row
import exe.tigrulya.relohome.notifier.telegram.bot.ext.onCommandWithErrorHandling
import exe.tigrulya.relohome.notifier.telegram.bot.ext.senderId

suspend fun BehaviourContext.handleStartCommand() = onCommandWithErrorHandling("start") { message ->
    send(
        chatId = message.senderId(),
        text = "Choose language",
        replyMarkup = setLocaleKeyboard()
    )
}

// TODO get supported locales from server
fun setLocaleKeyboard(): InlineKeyboardMarkup = inlineKeyboard {
    row {
        dataButton("\uD83C\uDDF7\uD83C\uDDFA Russian", LOCALE_VALUE_CALLBACK_DATA_PREFIX + "ru")
    }
    row {
        dataButton("\uD83C\uDDEC\uD83C\uDDE7 English", LOCALE_VALUE_CALLBACK_DATA_PREFIX + "en")
    }
}
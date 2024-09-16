package exe.tigrulya.relohome.notifier.telegram.bot.handlers

import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import exe.tigrulya.relohome.notifier.telegram.bot.KeyboardFactory.Companion.CHANGE_LOCALE_BUTTON_PREFIX
import exe.tigrulya.relohome.notifier.telegram.bot.ReloHomeContext
import exe.tigrulya.relohome.notifier.telegram.bot.ext.onTextStartingWith
import exe.tigrulya.relohome.notifier.telegram.bot.ext.sender
import exe.tigrulya.relohome.notifier.telegram.bot.ext.senderId

suspend fun BehaviourContext.handleSetLocale(ctx: ReloHomeContext) =
    onTextStartingWith(CHANGE_LOCALE_BUTTON_PREFIX) { message ->
        with(ctx) {
            withLocalization(message.sender()) {
                send(
                    chatId = message.senderId(),
                    text = constant("handlers.set-locale.question-message"),
                    replyMarkup = keyboardFactory.setLocaleInlineKeyboard()
                )
            }
        }
    }
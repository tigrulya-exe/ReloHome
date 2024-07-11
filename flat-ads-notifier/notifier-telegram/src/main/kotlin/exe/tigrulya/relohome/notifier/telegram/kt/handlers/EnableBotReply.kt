package exe.tigrulya.relohome.notifier.telegram.kt.handlers

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import exe.tigrulya.relohome.api.user_handler.UserHandlerGateway
import exe.tigrulya.relohome.notifier.telegram.kt.*
import exe.tigrulya.relohome.notifier.telegram.kt.env.enableErrorHandling
import exe.tigrulya.relohome.notifier.telegram.kt.env.reply
import exe.tigrulya.relohome.notifier.telegram.kt.env.senderId
import exe.tigrulya.relohome.notifier.telegram.kt.env.withSimpleErrorHandling

fun Dispatcher.enableBotReply(
    userHandlerGateway: UserHandlerGateway,
    keyboardProvider: MainKeyboardProvider
) = messageStartingWithPrefix(
    MainKeyboardProvider.ENABLED_BOT_BUTTON_TEXT,
    MainKeyboardProvider.DISABLED_BOT_BUTTON_TEXT
) {
    enableErrorHandling {
        val searchEnabled = withSimpleErrorHandling("Error enabling search") {
            userHandlerGateway.toggleSearch(senderId)
        }

        reply(
            message = "The flat ad search is ${if (searchEnabled) "enabled" else "disabled"}",
            replyMarkup = keyboardProvider.get(senderId, searchEnabled)
        )
    }
}

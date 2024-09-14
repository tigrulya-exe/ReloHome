package exe.tigrulya.relohome.notifier.telegram.bot.handlers

import dev.inmo.tgbotapi.extensions.api.send.send
import exe.tigrulya.relohome.notifier.telegram.bot.ReloHomeContext
import exe.tigrulya.relohome.notifier.telegram.bot.ext.onCommandWithErrorHandling
import exe.tigrulya.relohome.notifier.telegram.bot.ext.sender
import exe.tigrulya.relohome.notifier.telegram.bot.ext.senderId
import exe.tigrulya.relohome.notifier.telegram.bot.state.repo.UserState

suspend fun ReloHomeContext.handleStartCommand() = onCommandWithErrorHandling("start") { message ->
    withLocalization(message.sender()) {
        send(
            chatId = message.senderId(),
            text = constant("handlers.set-locale.question-message"),
            replyMarkup = setLocaleKeyboard()
        )

        onlyIfNoState(message.sender()) {
            transition(message.sender(), UserState.NEW)
        }
    }
}
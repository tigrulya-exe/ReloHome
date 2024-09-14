package exe.tigrulya.relohome.notifier.telegram.bot.handlers

import dev.inmo.tgbotapi.extensions.api.send.send
import exe.tigrulya.relohome.notifier.telegram.bot.ReloHomeContext
import exe.tigrulya.relohome.notifier.telegram.bot.ext.onCommandWithErrorHandling
import exe.tigrulya.relohome.notifier.telegram.bot.ext.sender
import exe.tigrulya.relohome.notifier.telegram.bot.ext.senderId
import exe.tigrulya.relohome.notifier.telegram.bot.state.UserState

suspend fun ReloHomeContext.handleStartCommand() = onCommandWithErrorHandling("start") { message ->
    send(
        chatId = message.senderId(),
        text = "Choose language",
        replyMarkup = setLocaleKeyboard()
    )

    userStatesManager.onlyIfNoState(message.sender()) {
        transition(message.sender(), UserState.NEW)
    }
}
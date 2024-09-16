package exe.tigrulya.relohome.notifier.telegram.bot.handlers

import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import exe.tigrulya.relohome.notifier.telegram.bot.ReloHomeContext
import exe.tigrulya.relohome.notifier.telegram.bot.ext.onCommandWithErrorHandling
import exe.tigrulya.relohome.notifier.telegram.bot.ext.sender
import exe.tigrulya.relohome.notifier.telegram.bot.ext.senderId
import exe.tigrulya.relohome.notifier.telegram.bot.state.repo.UserState

suspend fun BehaviourContext.handleStartCommand(ctx: ReloHomeContext) = onCommandWithErrorHandling("start") { message ->
    with(ctx) {
        withLocalization(message.sender()) {
            send(
                chatId = message.senderId(),
                text = constant("handlers.set-locale.question-message"),
                replyMarkup = keyboardFactory.setLocaleInlineKeyboard()
            )

            onlyIfNoState(message.sender()) {
                transition(message.sender(), UserState.NEW)
            }
        }
    }
}
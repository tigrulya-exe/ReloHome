package exe.tigrulya.relohome.notifier.telegram.bot.ability

import exe.tigrulya.relohome.api.user_handler.AsyncUserHandlerGateway
import exe.tigrulya.relohome.model.City
import exe.tigrulya.relohome.model.UserCreateDto
import exe.tigrulya.relohome.notifier.telegram.bot.ReloHomeBot
import exe.tigrulya.relohome.notifier.telegram.util.asCode
import org.telegram.abilitybots.api.objects.MessageContext
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import java.util.concurrent.CompletableFuture

class StartAbility(
    private val userHandlerGateway: AsyncUserHandlerGateway,
) : ReloHomeAbility {
    override val name: String = "start"

    override fun action(context: MessageContext) {
        val message = context.update().message

        registerUser(message)
            .thenApply { setLocation(message) }
            .handle { _, exception -> sendMessage(context, exception) }
    }

    private fun registerUser(message: Message): CompletableFuture<Unit> {
        return userHandlerGateway.registerUser(
            UserCreateDto(
                name = message.from.userName,
                externalId = message.from.id.toString()
            )
        )
    }

    private fun setLocation(message: Message): CompletableFuture<Unit> {
        return userHandlerGateway.setLocation(
            message.from.id.toString(),
            City(
                name = "Tbilisi",
                country = "Georgia"
            )
        )
    }

    private fun sendMessage(context: MessageContext, exception: Throwable?): CompletableFuture<Message> {
        val message = context.update().message

        val successText = "Hello there! Welcome to our apartment hunting bot! " +
                "We've designed this bot to keep an eye on all the major real estate listing sites for you. " +
                "Once we spot an apartment that matches your preferences, we'll shoot you a notification right away. \n\n" +
                "To make sure you receive only relevant ads, just click on the \n " +
                "${ReloHomeBot.OPTIONS_BUTTON_TEXT.asCode()} button below to set your preferences."

        val sendMessage = SendMessage().apply {
            chatId = message.from.id.toString()
            parseMode = "HTML"
            replyMarkup = unwrapBot(context).replyKeyboard(message.from.id.toString())
            text = exception?.let {
                "Error during registering user: ${it.message}"
            } ?: successText
        }

        return context.bot().executeAsync(sendMessage)
    }
}
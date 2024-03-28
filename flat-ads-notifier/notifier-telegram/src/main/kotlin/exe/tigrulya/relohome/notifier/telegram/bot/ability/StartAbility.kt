package exe.tigrulya.relohome.notifier.telegram.bot.ability

import exe.tigrulya.relohome.api.user_handler.BlockingUserHandlerGateway
import exe.tigrulya.relohome.model.UserCreateDto
import exe.tigrulya.relohome.notifier.telegram.bot.ReloHomeBot
import exe.tigrulya.relohome.notifier.telegram.util.asCode
import org.telegram.abilitybots.api.objects.MessageContext
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

class StartAbility(
    private val userHandlerGateway: BlockingUserHandlerGateway,
) : ReloHomeAbility {
    override val name: String = "start"

    override fun action(context: MessageContext) {
        val message = context.update().message

        // todo replace with async communication
        val registerResult = userHandlerGateway.registerUser(
            UserCreateDto(
                name = message.from.userName,
                externalId = message.from.id.toString()
            )
        )

        val successText = "Hello there! Welcome to our apartment hunting bot! " +
                "We've designed this bot to keep an eye on all the major real estate listing sites for you. " +
                "Once we spot an apartment that matches your preferences, we'll shoot you a notification right away. \n\n" +
                "To make sure you receive only relevant ads, just click on the \n " +
                "${ReloHomeBot.OPTIONS_BUTTON_TEXT.asCode()} button below to set your preferences."

        val sendMessage = SendMessage().apply {
            chatId = message.from.id.toString()
            parseMode = "HTML"
            replyMarkup = unwrapBot(context).replyKeyboard(message.from.id.toString())
            text = registerResult.fold({ successText }) {
                "Error during registering user: ${it.message}"
            }
        }

        context.bot().execute(sendMessage)
    }

}
package exe.tigrulya.relohome.notifier.telegram.bot.ability

import exe.tigrulya.relohome.api.UserHandlerGateway
import exe.tigrulya.relohome.model.UserCreateDto
import exe.tigrulya.relohome.notifier.telegram.bot.ReloHomeBot
import exe.tigrulya.relohome.notifier.telegram.bot.reply.EnableBotReply
import exe.tigrulya.relohome.notifier.telegram.bot.reply.StatisticsReply
import exe.tigrulya.relohome.notifier.telegram.bot.reply.SubscriptionInfoReply
import exe.tigrulya.relohome.notifier.telegram.util.asCode
import kotlinx.coroutines.runBlocking
import org.telegram.abilitybots.api.objects.MessageContext
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo

class StartAbility(
    private val userHandlerGateway: UserHandlerGateway,
    private val handlerWebUrl: String,
) : ReloHomeAbility {
    override val name: String = "start"

    override fun action(context: MessageContext) {
        val message = context.update().message

        val sendMessage = SendMessage().apply {
            chatId = message.from.id.toString()
            parseMode = "HTML"
            replyMarkup = replyKeyboard(message.from.id)
            text = "Hello there! Welcome to our apartment hunting bot! " +
                    "We've designed this bot to keep an eye on all the major real estate listing sites for you. " +
                    "Once we spot an apartment that matches your preferences, we'll shoot you a notification right away. \n\n" +
                    "To make sure you receive only relevant ads, just click on the \n " +
                    "${ReloHomeBot.OPTIONS_BUTTON_TEXT.asCode()} button below to set your preferences."
        }

        // todo replace with async communication
        runBlocking {
            userHandlerGateway.registerUser(UserCreateDto(
                name = message.from.userName,
                externalId = message.from.id.toString()
            ))
        }

        context.bot().execute(sendMessage)
    }

    private fun replyKeyboard(userId: Long): ReplyKeyboardMarkup {
        val enableButton = KeyboardButton.builder()
            .text(EnableBotReply.ENABLE_BUTTON_TEXT)
            .build()

        val webappButton = KeyboardButton.builder()
            .text(ReloHomeBot.OPTIONS_BUTTON_TEXT)
            .webApp(
                WebAppInfo.builder()
                    .url("${handlerWebUrl}/forms/tg_form/$userId")
                    .build()
            )
            .build()

        val subscriptionInfoButton = KeyboardButton.builder()
            .text(SubscriptionInfoReply.SUBSCRIPTION_INFO_BUTTON_TEXT)
            .build()

        val statisticsButton = KeyboardButton.builder()
            .text(StatisticsReply.STATISTICS_BUTTON_TEXT)
            .build()

        return ReplyKeyboardMarkup.builder()
            .keyboardRow(KeyboardRow(listOf(enableButton, webappButton)))
            .keyboardRow(KeyboardRow(listOf(subscriptionInfoButton, statisticsButton)))
            .build()
    }

}
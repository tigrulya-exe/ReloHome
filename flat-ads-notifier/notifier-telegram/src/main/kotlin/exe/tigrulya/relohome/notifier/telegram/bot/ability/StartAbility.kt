package exe.tigrulya.relohome.notifier.telegram.bot.ability

import exe.tigrulya.relohome.notifier.telegram.bot.ReloHomeBot
import org.telegram.abilitybots.api.objects.MessageContext
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo

class StartAbility(
    private val handlerWebUrl: String,
) : ReloHomeAbility {
    override val name: String = "start"

    override fun action(context: MessageContext) {
        val message = context.update().message

        val sendMessage = SendMessage().apply {
            chatId = message.from.id.toString()
            parseMode = "HTML"
            replyMarkup = replyKeyboard(message.from.id)
            text = "Hello, my friend! ReloHome bot will help you to find the best apartments in whole Georgia! " +
                    "At first provide search options by pressing 'Options' button below"
        }

        context.bot().execute(sendMessage)
    }

    private fun replyKeyboard(userId: Long): ReplyKeyboardMarkup {
        val enableButton = KeyboardButton.builder()
            // todo tmp
            .text("/" + ReloHomeBot.ENABLE_BUTTON_TEXT)
            .build()

        val webappButton = KeyboardButton.builder()
            .text("/" + ReloHomeBot.OPTIONS_BUTTON_TEXT)
            .webApp(
                WebAppInfo.builder()
                    .url("${handlerWebUrl}/forms/tg_form/$userId")
                    .build()
            )
            .build()

        val subscriptionInfoButton = KeyboardButton.builder()
            .text("/" + ReloHomeBot.SUBSCRIPTION_INFO_BUTTON_TEXT)
            .build()

        val statisticsButton = KeyboardButton.builder()
            .text("/" + ReloHomeBot.STATISTICS_BUTTON_TEXT)
            .build()

        return ReplyKeyboardMarkup.builder()
            .keyboardRow(KeyboardRow(listOf(enableButton, webappButton)))
            .keyboardRow(KeyboardRow(listOf(subscriptionInfoButton, statisticsButton)))
            .build()
    }

}
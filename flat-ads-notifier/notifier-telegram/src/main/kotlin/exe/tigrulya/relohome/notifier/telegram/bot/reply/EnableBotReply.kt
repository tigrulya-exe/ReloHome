package exe.tigrulya.relohome.notifier.telegram.bot.reply

import exe.tigrulya.relohome.api.UserHandlerGateway
import kotlinx.coroutines.runBlocking
import org.telegram.abilitybots.api.bot.BaseAbilityBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

class EnableBotReply(
    private val userHandlerGateway: UserHandlerGateway
) : ReloHomeReply {
    companion object {
        const val ENABLED_BOT_BUTTON_TEXT = "🟢 Disable bot"
        const val DISABLED_BOT_BUTTON_TEXT = "🔴 Enable bot"
    }

    override val name: String = ENABLED_BOT_BUTTON_TEXT

    override fun action(bot: BaseAbilityBot, update: Update): Unit = runBlocking {
        val userId = update.message.from.id.toString()
        val searchEnabled = userHandlerGateway.toggleSearch(userId)

        val sendMessage = SendMessage().apply {
            chatId = userId
            parseMode = "HTML"
            replyMarkup = unwrapBot(bot).replyKeyboard(userId, searchEnabled)
            text = "The flat ad search is ${if (searchEnabled) "enabled" else "disabled"}"
        }

        bot.execute(sendMessage)
    }
}
package exe.tigrulya.relohome.notifier.telegram.bot.reply

import exe.tigrulya.relohome.api.user_handler.AsyncUserHandlerGateway
import exe.tigrulya.relohome.api.user_handler.realMessage
import org.telegram.abilitybots.api.bot.BaseAbilityBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

class EnableBotReply(
    private val userHandlerGateway: AsyncUserHandlerGateway
) : ReloHomeReply {
    companion object {
        const val ENABLED_BOT_BUTTON_TEXT = "🟢 Disable bot"
        const val DISABLED_BOT_BUTTON_TEXT = "🔴 Enable bot"

        private val POSSIBLE_PREFIXES = listOf(ENABLED_BOT_BUTTON_TEXT, DISABLED_BOT_BUTTON_TEXT)
    }

    override val name: String = ENABLED_BOT_BUTTON_TEXT

    override fun matches(update: Update): Boolean = POSSIBLE_PREFIXES
        .any { update.message?.text?.startsWith(it) ?: false }

    override fun action(bot: BaseAbilityBot, update: Update) {
        val userId = update.message.from.id.toString()

        userHandlerGateway.toggleSearch(userId)
            .thenCompose { searchEnabled ->
                val sendMessage = SendMessage().apply {
                    chatId = userId
                    parseMode = "HTML"
                    replyMarkup = unwrapBot(bot).replyKeyboard(userId, searchEnabled)
                    text = "The flat ad search is ${if (searchEnabled) "enabled" else "disabled"}"
                }

                bot.executeAsync(sendMessage)
            }
            .exceptionallyCompose {
                bot.replyText(update, "Error toggling bot: ${it.realMessage}")
            }
    }
}
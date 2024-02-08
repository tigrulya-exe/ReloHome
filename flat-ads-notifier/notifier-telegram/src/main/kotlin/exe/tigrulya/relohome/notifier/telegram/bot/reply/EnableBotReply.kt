package exe.tigrulya.relohome.notifier.telegram.bot.reply

import org.telegram.abilitybots.api.bot.BaseAbilityBot
import org.telegram.telegrambots.meta.api.objects.Update

class EnableBotReply : ReloHomeReply {
    companion object {
        const val ENABLE_BUTTON_TEXT = "🟢 Enable bot"
    }

    override val name: String = ENABLE_BUTTON_TEXT

    override fun action(bot: BaseAbilityBot, update: Update) {
        bot.replyText(update, "Coming soon...")
    }
}
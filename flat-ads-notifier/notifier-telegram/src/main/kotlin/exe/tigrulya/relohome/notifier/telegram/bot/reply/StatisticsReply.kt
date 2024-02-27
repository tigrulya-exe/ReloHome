package exe.tigrulya.relohome.notifier.telegram.bot.reply

import org.telegram.abilitybots.api.bot.BaseAbilityBot
import org.telegram.telegrambots.meta.api.objects.Update

class StatisticsReply : ReloHomeReply {
    companion object {
        const val STATISTICS_BUTTON_TEXT = "📈 Statistics"
    }

    override val name: String = STATISTICS_BUTTON_TEXT

    override fun action(bot: BaseAbilityBot, update: Update) {
        bot.replyText(update, "Collecting enough data to build statistics, come back later")
    }
}
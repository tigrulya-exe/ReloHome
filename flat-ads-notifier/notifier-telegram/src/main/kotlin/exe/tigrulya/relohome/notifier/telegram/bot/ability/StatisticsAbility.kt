package exe.tigrulya.relohome.notifier.telegram.bot.ability

import exe.tigrulya.relohome.notifier.telegram.bot.ReloHomeBot.Companion.STATISTICS_BUTTON_TEXT
import org.telegram.abilitybots.api.objects.MessageContext

class StatisticsAbility : ReloHomeAbility {

    override val name: String = STATISTICS_BUTTON_TEXT

    override fun action(context: MessageContext) {
        context.replyText("Coming soon...")
    }
}
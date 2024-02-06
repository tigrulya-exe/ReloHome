package exe.tigrulya.relohome.notifier.telegram.bot.ability

import exe.tigrulya.relohome.notifier.telegram.bot.ReloHomeBot.Companion.SUBSCRIPTION_INFO_BUTTON_TEXT
import org.telegram.abilitybots.api.objects.MessageContext

class SubscriptionInfoAbility : ReloHomeAbility {

    override val name: String = SUBSCRIPTION_INFO_BUTTON_TEXT

    override fun action(context: MessageContext) {
        context.replyText("Coming soon...")
    }
}
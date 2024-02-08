package exe.tigrulya.relohome.notifier.telegram.bot.reply

import org.telegram.abilitybots.api.bot.BaseAbilityBot
import org.telegram.telegrambots.meta.api.objects.Update

class SubscriptionInfoReply : ReloHomeReply {
    companion object {
        const val SUBSCRIPTION_INFO_BUTTON_TEXT = "💳 Subscription info"
    }

    override val name: String = SUBSCRIPTION_INFO_BUTTON_TEXT

    override fun action(bot: BaseAbilityBot, update: Update) {
        bot.replyText(update, "Coming soon...")
    }
}
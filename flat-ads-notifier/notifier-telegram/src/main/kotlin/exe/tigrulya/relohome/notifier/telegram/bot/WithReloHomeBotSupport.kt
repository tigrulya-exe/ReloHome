package exe.tigrulya.relohome.notifier.telegram.bot

import org.telegram.abilitybots.api.bot.BaseAbilityBot
import org.telegram.abilitybots.api.objects.MessageContext

interface WithReloHomeBotSupport {
    fun unwrapBot(bot: BaseAbilityBot): ReloHomeBot = bot as ReloHomeBot

    fun unwrapBot(context: MessageContext): ReloHomeBot = unwrapBot(context.bot())
}
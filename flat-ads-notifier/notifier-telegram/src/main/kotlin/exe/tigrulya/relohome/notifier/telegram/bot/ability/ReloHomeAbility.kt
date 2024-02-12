package exe.tigrulya.relohome.notifier.telegram.bot.ability

import exe.tigrulya.relohome.notifier.telegram.bot.WithReloHomeBotSupport
import org.telegram.abilitybots.api.objects.Ability
import org.telegram.abilitybots.api.objects.Locality
import org.telegram.abilitybots.api.objects.MessageContext
import org.telegram.abilitybots.api.objects.Privacy
import org.telegram.abilitybots.api.util.AbilityExtension
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

interface ReloHomeAbility : AbilityExtension, WithReloHomeBotSupport {
    val name: String

    fun ability(): Ability {
        return Ability
            .builder()
            .name(name)
            .locality(Locality.ALL)
            .privacy(Privacy.PUBLIC)
            .action { action(it) }
            .enableStats()
            .build()
    }

    fun action(context: MessageContext)

    fun MessageContext.replyText(text: String, formatted: Boolean = false) {
        val message = SendMessage()
        message.setChatId(user().id)
        message.text = text
        message.enableMarkdown(formatted)

        bot().execute(message)
    }
}
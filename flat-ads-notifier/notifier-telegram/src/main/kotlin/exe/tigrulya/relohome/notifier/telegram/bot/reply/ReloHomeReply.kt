package exe.tigrulya.relohome.notifier.telegram.bot.reply

import org.telegram.abilitybots.api.bot.BaseAbilityBot
import org.telegram.abilitybots.api.objects.Reply
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

interface ReloHomeReply {
    val name: String

    fun matches(update: Update): Boolean = update.message.text.startsWith(name)

    fun action(bot: BaseAbilityBot, update: Update)

    fun reply(): Reply = Reply.of(::action, ::matches)

    fun BaseAbilityBot.replyText(update: Update, text: String, formatted: Boolean = false) {
        val message = SendMessage()
        message.setChatId(update.message.from.id)
        message.text = text
        message.enableMarkdown(formatted)

        execute(message)
    }
}
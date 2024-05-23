package exe.tigrulya.relohome.notifier.telegram.bot.reply

import exe.tigrulya.relohome.notifier.telegram.bot.WithReloHomeBotSupport
import org.telegram.abilitybots.api.bot.BaseAbilityBot
import org.telegram.abilitybots.api.objects.Reply
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.concurrent.CompletableFuture

interface ReloHomeReply: WithReloHomeBotSupport {
    val name: String

    fun matches(update: Update): Boolean = update.message.text.startsWith(name)

    fun action(bot: BaseAbilityBot, update: Update)

    fun reply(): Reply = Reply.of(::action, ::matches)

    fun BaseAbilityBot.replyText(
        update: Update,
        text: String,
        formatted: Boolean = false
    ): CompletableFuture<Message> {
        val message = SendMessage()
        message.setChatId(update.message.from.id)
        message.text = text
        message.enableMarkdown(formatted)

        return executeAsync(message)
    }
}
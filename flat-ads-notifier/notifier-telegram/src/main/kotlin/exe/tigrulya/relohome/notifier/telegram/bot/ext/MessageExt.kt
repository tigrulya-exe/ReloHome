package exe.tigrulya.relohome.notifier.telegram.bot.ext

import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.chat.User
import dev.inmo.tgbotapi.types.message.abstracts.Message

fun User.externalId(): String = id.chatId.toString()

fun Message.sender(): String = chat.id.chatId.toString()
fun Message.senderId(): IdChatIdentifier = chat.id

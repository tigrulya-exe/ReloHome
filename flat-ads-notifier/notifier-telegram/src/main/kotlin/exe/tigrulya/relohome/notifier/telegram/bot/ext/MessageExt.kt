package exe.tigrulya.relohome.notifier.telegram.bot.ext

import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.chat.User
import dev.inmo.tgbotapi.types.message.abstracts.Message
import dev.inmo.tgbotapi.types.queries.callback.MessageDataCallbackQuery

fun User.externalId(): String = id.chatId.toString()

fun Message.sender(): String = chat.id.chatId.toString()
fun Message.senderId(): IdChatIdentifier = chat.id

fun MessageDataCallbackQuery.sender(): String = from.id.chatId.toString()
fun MessageDataCallbackQuery.senderId(): IdChatIdentifier = from.id

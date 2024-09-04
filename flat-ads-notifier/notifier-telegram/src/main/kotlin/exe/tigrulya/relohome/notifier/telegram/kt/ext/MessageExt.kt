package exe.tigrulya.relohome.notifier.telegram.kt.ext

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage

fun CommonMessage<*>.sender(): String = chat.id.toString()
fun CommonMessage<*>.senderId(): IdChatIdentifier = chat.id

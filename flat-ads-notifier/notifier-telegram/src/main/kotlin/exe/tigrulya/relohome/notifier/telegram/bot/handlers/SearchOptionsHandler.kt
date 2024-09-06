package exe.tigrulya.relohome.notifier.telegram.bot.handlers

import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import exe.tigrulya.relohome.api.user_handler.UserHandlerGateway
import exe.tigrulya.relohome.model.NumRange
import exe.tigrulya.relohome.model.UserSearchOptionsDto
import exe.tigrulya.relohome.notifier.telegram.bot.ext.onWebappWithErrorHandling
import exe.tigrulya.relohome.notifier.telegram.bot.ext.withSimpleErrorHandling
import exe.tigrulya.relohome.notifier.telegram.serde.SearchOptionsDeserializer

suspend fun BehaviourContext.handleSearchOptions(
    userHandlerGateway: UserHandlerGateway,
    searchOptionsDeserializer: SearchOptionsDeserializer
) = onWebappWithErrorHandling { message ->

    val searchOptions = searchOptionsDeserializer.deserialize(message.chatEvent.data)

    withSimpleErrorHandling(message.chat.id, "Error setting search options") {
        userHandlerGateway.setSearchOptions(message.chat.id.chatId.toString(), searchOptions)
    }

    send(
        chatId = message.chat.id,
        text = "Successfully updated search parameters: \n\n${searchOptions.orUnset()}",
        parseMode = MarkdownParseMode
    )
}

fun UserSearchOptionsDto.orUnset(): String = """
    *Prices:* ${priceRange.orUnset()}
    *Rooms:* ${roomRange.orUnset()}
    *Bedrooms:* ${bedroomRange.orUnset()}
    *Floor:* ${floorRange.orUnset()}
    *Area:* ${areaRange.orUnset()}
    *SubDistricts:* ${subDistricts.orUnset()}
""".trimIndent()

fun NumRange.orUnset(): String = if (from == null && to == null) {
    "unset"
} else {
    "${from.orUnset()} - ${to.orUnset()}"
}

fun Any?.orUnset() = this?.toString() ?: "unset"
fun Collection<*>.orUnset() = if (isEmpty()) "unset" else toString()
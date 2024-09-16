package exe.tigrulya.relohome.notifier.telegram.bot.handlers

import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import exe.tigrulya.relohome.model.NumRange
import exe.tigrulya.relohome.model.UserSearchOptionsDto
import exe.tigrulya.relohome.notifier.telegram.bot.ReloHomeContext
import exe.tigrulya.relohome.notifier.telegram.bot.ext.onWebappWithErrorHandling
import exe.tigrulya.relohome.notifier.telegram.bot.ext.sender
import exe.tigrulya.relohome.notifier.telegram.bot.ext.withSimpleErrorHandling
import exe.tigrulya.relohome.notifier.telegram.bot.state.repo.UserState

suspend fun BehaviourContext.handleSearchOptions(ctx: ReloHomeContext) = onWebappWithErrorHandling { message ->
    with(ctx) {
        withLocalization(message.sender()) {
            val searchOptions = searchOptionsDeserializer.deserialize(message.chatEvent.data)

            withSimpleErrorHandling(message.chat.id, constant("handlers.set-search-options.error")) {
                userHandlerGateway.setSearchOptions(message.chat.id.chatId.toString(), searchOptions)
            }

            userStatesManager.transition(message.sender(), UserState.SEARCH_OPTIONS_PROVIDED)

            send(
                chatId = message.chat.id,
                text = constant("handlers.set-search-options.success", searchOptions.toConstantCtx()),
                parseMode = MarkdownParseMode,
                replyMarkup = keyboardFactory.mainReplyKeyboard(message.sender(), locale, searchEnabled = true)
            )
        }
    }
}

fun UserSearchOptionsDto.toConstantCtx(): Map<String, String> = mapOf(
    "prices" to priceRange.orUnset(),
    "rooms" to roomRange.orUnset(),
    "bedrooms" to bedroomRange.orUnset(),
    "floor" to floorRange.orUnset(),
    "area" to areaRange.orUnset(),
    "subDistricts" to subDistricts.orUnset()
)

fun NumRange.orUnset(): String = if (from == null && to == null) {
    "x"
} else {
    "${from.orUnset()} - ${to.orUnset()}"
}

fun Any?.orUnset() = this?.toString() ?: "x"
fun Collection<*>.orUnset() = if (isEmpty()) "x" else toString()
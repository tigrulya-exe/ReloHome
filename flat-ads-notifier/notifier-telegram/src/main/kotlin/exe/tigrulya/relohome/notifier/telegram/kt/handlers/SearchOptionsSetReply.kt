package exe.tigrulya.relohome.notifier.telegram.kt.handlers

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import exe.tigrulya.relohome.api.user_handler.UserHandlerGateway
import exe.tigrulya.relohome.model.NumRange
import exe.tigrulya.relohome.model.UserSearchOptionsDto
import exe.tigrulya.relohome.notifier.telegram.kt.MainKeyboardProvider
import exe.tigrulya.relohome.notifier.telegram.kt.env.*
import exe.tigrulya.relohome.notifier.telegram.kt.messageWithWebApp
import exe.tigrulya.relohome.notifier.telegram.serde.SearchOptionsDeserializer

fun Dispatcher.searchOptionsSetReply(
    userHandlerGateway: UserHandlerGateway,
    searchOptionsDeserializer: SearchOptionsDeserializer,
    keyboardProvider: MainKeyboardProvider
) = messageWithWebApp {

    enableErrorHandling {
        message.webAppData?.data?.let {
            val searchOptions = searchOptionsDeserializer.deserialize(it)

            withSimpleErrorHandling("Error setting search options") {
                userHandlerGateway.setSearchOptions(senderId, searchOptions)
            }

            reply(
                message = "Successfully sent data from web app to server: ${searchOptions.orUnset()}",
                replyMarkup = keyboardProvider.get(senderId)
            )
        }
    }
}

fun UserSearchOptionsDto.orUnset(): String = """
    Prices: ${priceRange.orUnset()}
    Rooms: ${roomRange.orUnset()}
    Area: ${areaRange.orUnset()}
    SubDistricts: $subDistricts
""".trimIndent()

fun NumRange.orUnset(): String = if (from == null && to == null) {
    "unset"
} else {
    "${from.orUnset()} - ${to.orUnset()}"
}

fun Any?.orUnset() = this?.toString() ?: "unset"
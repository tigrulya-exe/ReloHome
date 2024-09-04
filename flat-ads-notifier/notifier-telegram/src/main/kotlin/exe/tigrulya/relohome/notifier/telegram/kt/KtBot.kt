package exe.tigrulya.relohome.notifier.telegram.kt

import dev.inmo.micro_utils.coroutines.subscribeSafely
import dev.inmo.micro_utils.coroutines.subscribeSafelyWithoutExceptions
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.telegramBotWithBehaviourAndLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.utils.whenFromUser
import exe.tigrulya.relohome.api.user_handler.UserHandlerGateway
import exe.tigrulya.relohome.model.City
import exe.tigrulya.relohome.model.UserCreateDto
import exe.tigrulya.relohome.model.UserSearchOptionsDto
import exe.tigrulya.relohome.notifier.telegram.kt.handlers.enableSearchReply
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val botToken = System.getenv("BOT_TOKEN")

    val mainKeyboardProvider = MainKeyboardProvider("https://example.com")

    telegramBotWithBehaviourAndLongPolling(botToken, CoroutineScope(Dispatchers.IO)) {
        onCommand("start") {
            reply(it, "Hello, ${it.chat.id}")
            send(it.chat.id, "ert")
        }
        enableSearchReply(TestGateway, mainKeyboardProvider)

        allUpdatesFlow.subscribeSafely(this) { println(it) }
    }.second.join()
}

object TestGateway : UserHandlerGateway {
    var enabled = true

    override suspend fun registerUser(user: UserCreateDto) {
        TODO("Not yet implemented")
    }

    override suspend fun setLocation(externalId: String, city: City) {
        TODO("Not yet implemented")
    }

    override suspend fun setSearchOptions(externalUserId: String, searchOptions: UserSearchOptionsDto) {
        TODO("Not yet implemented")
    }

    override suspend fun toggleSearch(externalId: String): Boolean {
//        throw RuntimeException("failll")
        println("Yuppy: $externalId")
        enabled = !enabled
        return enabled
    }

}
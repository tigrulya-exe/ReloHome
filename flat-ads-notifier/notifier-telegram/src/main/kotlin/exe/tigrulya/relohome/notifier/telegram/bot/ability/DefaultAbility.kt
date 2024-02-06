package exe.tigrulya.relohome.notifier.telegram.bot.ability

import exe.tigrulya.relohome.api.UserHandlerGateway
import exe.tigrulya.relohome.notifier.telegram.serde.JsonSearchOptionsDeserializer
import exe.tigrulya.relohome.notifier.telegram.serde.SearchOptionsDeserializer
import kotlinx.coroutines.runBlocking
import org.telegram.abilitybots.api.objects.MessageContext

class DefaultAbility(
    private val userHandlerGateway: UserHandlerGateway,
    private val searchOptionsDeserializer: SearchOptionsDeserializer = JsonSearchOptionsDeserializer(),
) : ReloHomeAbility {
    override val name: String = "default"

    override fun action(context: MessageContext) {
        val update = context.update()
        val message = update.message
        val source = message.from

        message.webAppData?.let {
            handleSearchOptions(source.id.toString(), it.data)
            context.replyText("Successfully sent data from web app to server: ${it.data}")
            return
        }

        context.replyText("Default handler, thread: ${Thread.currentThread().name}")
    }

    // todo mb use some coroutine-friendly tg bot framework
    private fun handleSearchOptions(userId: String, rawSearchOptions: String) = runBlocking {
        val searchOptions = searchOptionsDeserializer.deserialize(rawSearchOptions)
        userHandlerGateway.setSearchOptions(userId, searchOptions)
    }
}
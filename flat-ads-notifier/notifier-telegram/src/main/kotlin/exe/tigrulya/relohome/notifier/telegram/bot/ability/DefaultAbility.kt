package exe.tigrulya.relohome.notifier.telegram.bot.ability

import exe.tigrulya.relohome.api.user_handler.AsyncUserHandlerGateway
import exe.tigrulya.relohome.api.user_handler.realMessage
import exe.tigrulya.relohome.notifier.telegram.serde.JsonSearchOptionsDeserializer
import exe.tigrulya.relohome.notifier.telegram.serde.SearchOptionsDeserializer
import org.telegram.abilitybots.api.objects.MessageContext
import java.util.concurrent.CompletableFuture

class DefaultAbility(
    private val userHandlerGateway: AsyncUserHandlerGateway,
    private val searchOptionsDeserializer: SearchOptionsDeserializer = JsonSearchOptionsDeserializer(),
) : ReloHomeAbility {
    override val name: String = "default"

    override fun action(context: MessageContext) {
        val update = context.update()

        update.message.webAppData?.let {
            handleSearchOptions(context, it.data)
            return
        }

        context.replyText("Sorry, I don't understand what are you trying to say. I`m just a bot :)")
    }

    private fun handleSearchOptions(context: MessageContext, rawSearchOptions: String) {
        val message = context.update().message
        val searchOptions = searchOptionsDeserializer.deserialize(rawSearchOptions)

        CompletableFuture.runAsync { searchOptionsDeserializer.deserialize(rawSearchOptions) }
            .thenApply {
                userHandlerGateway.setSearchOptions(message.from.id.toString(), searchOptions)
            }
            .handle { _, error ->
                val replyText = error?.let {
                    "Error setting search options: ${it.realMessage}"
                } ?: "Successfully sent data from web app to server: $rawSearchOptions"
                context.replyText(replyText)
            }
    }
}
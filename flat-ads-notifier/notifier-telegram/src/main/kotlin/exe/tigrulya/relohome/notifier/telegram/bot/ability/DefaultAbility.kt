package exe.tigrulya.relohome.notifier.telegram.bot.ability

import exe.tigrulya.relohome.api.BlockingUserHandlerGateway
import exe.tigrulya.relohome.notifier.telegram.serde.JsonSearchOptionsDeserializer
import exe.tigrulya.relohome.notifier.telegram.serde.SearchOptionsDeserializer
import org.telegram.abilitybots.api.objects.MessageContext

class DefaultAbility(
    private val userHandlerGateway: BlockingUserHandlerGateway,
    private val searchOptionsDeserializer: SearchOptionsDeserializer = JsonSearchOptionsDeserializer(),
) : ReloHomeAbility {
    override val name: String = "default"

    override fun action(context: MessageContext) {
        val update = context.update()

        update.message.webAppData?.let {
            handleSearchOptions(context, it.data)
            return
        }

        context.replyText("Default handler, thread: ${Thread.currentThread().name}")
    }

    private fun handleSearchOptions(context: MessageContext, rawSearchOptions: String) {
        val message = context.update().message
        val searchOptions = searchOptionsDeserializer.deserialize(rawSearchOptions)

        val setOptionsResult = userHandlerGateway.setSearchOptions(message.from.id.toString(), searchOptions)
        val replyText = setOptionsResult.map { "Successfully sent data from web app to server: $rawSearchOptions" }
            .getOrElse { "Error setting search options: ${it.message}" }

        context.replyText(replyText)
    }
}
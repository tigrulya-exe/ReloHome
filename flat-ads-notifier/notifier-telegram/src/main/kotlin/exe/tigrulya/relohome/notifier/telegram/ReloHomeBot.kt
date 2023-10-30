package exe.tigrulya.relohome.notifier.telegram

import exe.tigrulya.relohome.api.FlatAdNotifierGateway
import exe.tigrulya.relohome.model.FlatAd
import exe.tigrulya.relohome.model.Image
import exe.tigrulya.relohome.model.User
import exe.tigrulya.relohome.util.LoggerProperty
import org.telegram.abilitybots.api.bot.AbilityBot
import org.telegram.abilitybots.api.objects.Ability
import org.telegram.abilitybots.api.objects.Locality
import org.telegram.abilitybots.api.objects.MessageContext
import org.telegram.abilitybots.api.objects.Privacy
import org.telegram.abilitybots.api.toggle.AbilityToggle
import org.telegram.abilitybots.api.toggle.CustomToggle
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto
import java.net.URL
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel
import java.util.concurrent.ThreadLocalRandom


class ReloHomeBot(botToken: String, botUsername: String, private val creatorId: Long) :
    AbilityBot(botToken, botUsername, createToggle(), createBotOptions()), FlatAdNotifierGateway {

    private val logger by LoggerProperty()

    override fun creatorId(): Long {
        return creatorId
    }

    fun defaultAbility(): Ability {
        return Ability
            .builder()
            .name(DEFAULT)
            .locality(Locality.ALL)
            .privacy(Privacy.PUBLIC)
            .action { context: MessageContext -> defaultAction(context) }
            .enableStats()
            .build()
    }

    private fun defaultAction(context: MessageContext) {
        val update = context.update()
        val message = update.message
        val source = message.from
        silent.send("Test, new beeeaach-" + ThreadLocalRandom.current().nextInt(), source.id)
    }

    companion object {
        private fun createToggle(): AbilityToggle {
            val customToggle = CustomToggle()
            customToggle.turnOff("demote")
            return customToggle
        }

        private fun createBotOptions(): DefaultBotOptions {
            val options = DefaultBotOptions()
            options.getUpdatesTimeout = 120
            return options
        }
    }

    override fun onNewAd(user: User, flatAd: FlatAd) {
        logger.info("Send ad ${flatAd.id} to ${user.id}")
        // todo move template to file
        val adText = """
            ${flatAd.title}
            
            ${flatAd.description}
            Price: ${flatAd.price}
            Link: ${flatAd.contacts.flatServiceLink}
        """.trimIndent()

        if (flatAd.images.isEmpty()) {
            sendAdWithoutImages(user.externalId, adText)
            return
        }

        sendAdWithImages(user.externalId, adText, flatAd.images)
    }

    private fun sendAdWithImages(userId: String, text: String, images: List<Image>) {
        val imagesGroup = images
            .take(10)
            .withIndex()
            .map {
                InputMediaPhoto().apply {
                    setMedia(
                        URL(it.value.url).openStream(),
                        it.index.toString()
                    )
                }
            }
        // little hack to attach text to group of images
        imagesGroup[0].caption = text

        val message = SendMediaGroup()
        message.chatId = userId
        message.medias = imagesGroup
        execute(message)
    }

    private fun sendAdWithoutImages(userId: String, text: String) {
        val message = SendMessage()
        message.chatId = userId
        message.text = text
        execute(message)
    }

    // todo check zero-copy methods
    private fun downloadImage(link: String) {
        val imageUrl = URL(link)
        val readableByteChannel: ReadableByteChannel = Channels.newChannel(imageUrl.openStream())
    }
}

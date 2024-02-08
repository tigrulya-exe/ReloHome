package exe.tigrulya.relohome.notifier.telegram.bot

import exe.tigrulya.relohome.api.FlatAdNotifierGateway
import exe.tigrulya.relohome.api.UserHandlerGateway
import exe.tigrulya.relohome.model.FlatAd
import exe.tigrulya.relohome.model.Image
import exe.tigrulya.relohome.notifier.telegram.bot.ability.*
import exe.tigrulya.relohome.notifier.telegram.bot.reply.*
import exe.tigrulya.relohome.notifier.telegram.serde.JsonSearchOptionsDeserializer
import exe.tigrulya.relohome.notifier.telegram.serde.SearchOptionsDeserializer
import exe.tigrulya.relohome.notifier.template.MustacheTemplateEngine
import exe.tigrulya.relohome.notifier.template.TemplateEngine
import exe.tigrulya.relohome.util.LoggerProperty
import org.telegram.abilitybots.api.bot.DefaultAbilities
import org.telegram.abilitybots.api.objects.Ability
import org.telegram.abilitybots.api.objects.Reply
import org.telegram.abilitybots.api.toggle.AbilityToggle
import org.telegram.abilitybots.api.toggle.CustomToggle
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto
import java.net.URL
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel

class ReloHomeBot(
    botToken: String,
    botUsername: String,
    private val creatorId: Long,
    private val userHandlerGateway: UserHandlerGateway,
    private val handlerWebUrl: String,
    private val searchOptionsDeserializer: SearchOptionsDeserializer = JsonSearchOptionsDeserializer(),
    private val templateEngine: TemplateEngine = MustacheTemplateEngine(),
    requestsPerSecond: Int = 10
) : RateLimitingAbilityBot(botToken, botUsername, createToggle(), createBotOptions(), requestsPerSecond),
    FlatAdNotifierGateway {

    private val logger by LoggerProperty()

    companion object {
        const val OPTIONS_BUTTON_TEXT = "⚙️ Change settings"

        private fun createToggle(): AbilityToggle {
            return CustomToggle().apply {
                turnOff(DefaultAbilities.CLAIM)
                turnOff(DefaultAbilities.DEMOTE)
                turnOff(DefaultAbilities.UNBAN)
                turnOff(DefaultAbilities.BACKUP)
                turnOff(DefaultAbilities.BAN)
                turnOff(DefaultAbilities.UNBAN)
                turnOff(DefaultAbilities.COMMANDS)
                turnOff(DefaultAbilities.PROMOTE)
                turnOff(DefaultAbilities.REPORT)
                turnOff(DefaultAbilities.STATS)
                turnOff(DefaultAbilities.RECOVER)
            }
        }

        private fun createBotOptions(): DefaultBotOptions {
            return DefaultBotOptions().apply {
                getUpdatesTimeout = 120
            }
        }
    }

    override fun creatorId(): Long {
        return creatorId
    }

    override fun getCommandPrefix(): String {
        return ""
    }

    fun defaultAbility(): Ability {
        return DefaultAbility(userHandlerGateway, searchOptionsDeserializer).ability()
    }

    fun startAbility(): Ability {
        return StartAbility(userHandlerGateway, handlerWebUrl).ability()
    }

    fun enableBotReply(): Reply {
        return EnableBotReply().reply()
    }

    fun subscriptionInfoReply(): Reply {
        return SubscriptionInfoReply().reply()
    }

    fun statisticsReply(): Reply {
        return StatisticsReply().reply()
    }

    override suspend fun onNewAd(userIds: List<String>, flatAd: FlatAd) {
        logger.info("Send ad ${flatAd.id} to $userIds")

        val flatAdMessage = templateEngine.compile("templates/new-flat-ad.mustache", flatAd)

        if (flatAd.images.isEmpty()) {
            // todo maybe send in thread-pool
            userIds.forEach { sendAdWithoutImages(it, flatAdMessage) }
            return
        }

        userIds.forEach { sendAdWithImages(it, flatAdMessage, flatAd.images) }
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
        imagesGroup[0].parseMode = "Markdown"

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
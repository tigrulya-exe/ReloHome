package exe.tigrulya.relohome.notifier.telegram.bot

import exe.tigrulya.relohome.api.FlatAdNotifierGateway
import exe.tigrulya.relohome.api.user_handler.AsyncUserHandlerGateway
import exe.tigrulya.relohome.model.Contacts
import exe.tigrulya.relohome.model.FlatAd
import exe.tigrulya.relohome.model.Image
import exe.tigrulya.relohome.notifier.telegram.bot.ability.DefaultAbility
import exe.tigrulya.relohome.notifier.telegram.bot.ability.StartAbility
import exe.tigrulya.relohome.notifier.telegram.bot.reply.EnableBotReply
import exe.tigrulya.relohome.notifier.telegram.bot.reply.StatisticsReply
import exe.tigrulya.relohome.notifier.telegram.bot.reply.SubscriptionInfoReply
import exe.tigrulya.relohome.notifier.telegram.serde.JsonSearchOptionsDeserializer
import exe.tigrulya.relohome.notifier.telegram.serde.SearchOptionsDeserializer
import exe.tigrulya.relohome.template.ObjectReuseMustacheTemplateEngine
import exe.tigrulya.relohome.template.TemplateEngine
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo
import java.net.URL
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel
import java.util.concurrent.CompletableFuture

// todo &amp
class ReloHomeBot(
    botToken: String,
    botUsername: String,
    private val creatorId: Long,
    private val userHandlerGateway: AsyncUserHandlerGateway,
    private val handlerWebUrl: String,
    private val searchOptionsDeserializer: SearchOptionsDeserializer = JsonSearchOptionsDeserializer(),
    private val templateEngine: TemplateEngine = ObjectReuseMustacheTemplateEngine(),
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
        return StartAbility(userHandlerGateway).ability()
    }

    fun enableBotReply(): Reply {
        return EnableBotReply(userHandlerGateway).reply()
    }

    fun subscriptionInfoReply(): Reply {
        return SubscriptionInfoReply().reply()
    }

    fun statisticsReply(): Reply {
        return StatisticsReply().reply()
    }

    // todo move to separate class
    override suspend fun onNewAd(userIds: List<String>, flatAd: FlatAd) {
        logger.info("Send ad ${flatAd.id} to $userIds")

        val flatAdMessage = compileFlatAdMessage(flatAd)

        if (flatAd.images.isEmpty()) {
            userIds.forEach { sendAdWithoutImages(it, flatAdMessage) }
            return
        }

        userIds.forEach { sendAdWithImages(it, flatAdMessage, flatAd.images) }
    }


    private fun compileFlatAdMessage(flatAd: FlatAd): String {
        val links = with(flatAd.contacts) {
             MessengerLinks(
                whatsAppLink = messengerIds[Contacts.Messenger.WHATSAPP]?.let {
                    "https://api.whatsapp.com/send/?phone=${it}"
                },
                viberLink = messengerIds[Contacts.Messenger.VIBER]?.let {
                    "viber://chat/?number=${it}"
                }
            )
        }
        val scopes = arrayOf(flatAd, links)
        return templateEngine.compile("templates/new-flat-ad.mustache", *scopes)
    }

    fun replyKeyboard(userId: String, isEnabled: Boolean = true): ReplyKeyboardMarkup {
        val enableButton = KeyboardButton.builder()
            .text(if (isEnabled) EnableBotReply.ENABLED_BOT_BUTTON_TEXT else EnableBotReply.DISABLED_BOT_BUTTON_TEXT)
            .build()

        val webappButton = KeyboardButton.builder()
            .text(OPTIONS_BUTTON_TEXT)
            .webApp(
                WebAppInfo.builder()
                    .url("${handlerWebUrl}/forms/tg_form/$userId")
                    .build()
            )
            .build()

        val subscriptionInfoButton = KeyboardButton.builder()
            .text(SubscriptionInfoReply.SUBSCRIPTION_INFO_BUTTON_TEXT)
            .build()

        val statisticsButton = KeyboardButton.builder()
            .text(StatisticsReply.STATISTICS_BUTTON_TEXT)
            .build()

        return ReplyKeyboardMarkup.builder()
            .keyboardRow(KeyboardRow(listOf(enableButton, webappButton)))
            .keyboardRow(KeyboardRow(listOf(subscriptionInfoButton, statisticsButton)))
            .build()
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
        imagesGroup[0].apply {
            caption = maybeShrink(text)
            parseMode = "Markdown"
        }

        val message = SendMediaGroup().apply {
            chatId = userId
            medias = imagesGroup
        }
        executeAsync(message)
            .onError { logger.error(it.message) }
    }

    // todo handle retries for Too Many Requests: retry after 10
    private fun sendAdWithoutImages(userId: String, messageText: String) {
        val message = SendMessage().apply {
            chatId = userId
            text = messageText
        }
        executeAsync(message)
            .onError { logger.error(it.message) }
    }

    // todo check zero-copy methods
    private fun downloadImage(link: String) {
        val imageUrl = URL(link)
        val readableByteChannel: ReadableByteChannel = Channels.newChannel(imageUrl.openStream())
    }

    private fun maybeShrink(text: String) = if (text.length >= 1024) {
        text.substring(0, 1020) + "..."
    } else text
}

fun CompletableFuture<*>.onError(handler: (Throwable) -> Unit) {
    handle { _, error ->
        error?.let { handler.invoke(it) }
    }
}

data class MessengerLinks(val whatsAppLink: String? = null, val viberLink: String? = null)
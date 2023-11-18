package exe.tigrulya.relohome.notifier.telegram

import exe.tigrulya.relohome.api.FlatAdNotifierGateway
import exe.tigrulya.relohome.api.UserHandlerGateway
import exe.tigrulya.relohome.model.FlatAd
import exe.tigrulya.relohome.model.Image
import exe.tigrulya.relohome.model.User
import exe.tigrulya.relohome.model.UserSearchOptionsDto
import exe.tigrulya.relohome.util.LoggerProperty
import kotlinx.coroutines.runBlocking
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo
import java.net.URL
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel
import java.util.concurrent.ThreadLocalRandom

class ReloHomeBot(
    botToken: String,
    botUsername: String,
    private val creatorId: Long,
    private val userHandlerGateway: UserHandlerGateway,
    private val searchOptionsDeserializer: SearchOptionsDeserializer
) :
    AbilityBot(botToken, botUsername, createToggle(), createBotOptions()), FlatAdNotifierGateway {

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

    private val logger by LoggerProperty()

    private val keyboardM1: InlineKeyboardMarkup
    private val keyboardM2: InlineKeyboardMarkup

    private val testKeyboardMarkup: ReplyKeyboardMarkup

    init {
        val next = InlineKeyboardButton.builder()
            .text("Next").callbackData("next")
            .build()

        val back = InlineKeyboardButton.builder()
            .text("Back").callbackData("back")
            .build()

        val url = InlineKeyboardButton.builder()
            .text("Tutorial")
            .url("https://core.telegram.org/bots/api")
            .build()

        keyboardM1 = InlineKeyboardMarkup.builder()
            .keyboardRow(listOf(next)).build()

        keyboardM2 = InlineKeyboardMarkup.builder()
            .keyboardRow(listOf(back))
            .keyboardRow(listOf(url))
            .build()


        val webappButton = KeyboardButton.builder()
            .text("Change settings")
            .webApp(
                WebAppInfo.builder()
                    .url("https://127.0.0.1:8787/new-user-form.html")
                    .build()
            )
            .build()

        testKeyboardMarkup = ReplyKeyboardMarkup.builder()
            .keyboardRow(
                KeyboardRow(listOf(webappButton))
            ).build()

//        execute(SetChatMenuButton().apply {
//            menuButton = MenuButtonWebApp.builder()
//                .text("Change settings")
//                .webAppInfo(
//                    WebAppInfo.builder()
//                        .url("https://127.0.0.1:8787/new-user-form.html")
//                        .build()
//                )
//                .build()
//        })
    }


    override fun creatorId(): Long {
        return creatorId
    }

    fun defaultAbility(): Ability {
        return Ability
            .builder()
            .name(DEFAULT)
            .locality(Locality.ALL)
            .privacy(Privacy.PUBLIC)
            .action { defaultAction(it) }
            .enableStats()
            .build()
    }

    fun showInlineMenuAbility(): Ability {
        return Ability
            .builder()
            .name("show_menu")
            .locality(Locality.ALL)
            .privacy(Privacy.PUBLIC)
            .action { showInlineMenuAction(it) }
            .enableStats()
            .build()
    }

    private fun showInlineMenuAction(context: MessageContext) {
        val message = context.update().message

        val sendMessage = SendMessage().apply {
            chatId = message.from.id.toString()
            parseMode = "HTML"
            replyMarkup = keyboardM1
            text = "inline yopta"
        }

        execute(sendMessage)
    }

    fun showReplyMenuAbility(): Ability {
        return Ability
            .builder()
            .name("show_reply_menu")
            .locality(Locality.ALL)
            .privacy(Privacy.PUBLIC)
            .action { showReplyMenuAction(it) }
            .enableStats()
            .build()
    }

    private fun showReplyMenuAction(context: MessageContext) {
        val message = context.update().message

        val sendMessage = SendMessage().apply {
            chatId = message.from.id.toString()
            parseMode = "HTML"
            text = "Hello, my friend!"
            replyMarkup = testKeyboardMarkup
        }

        execute(sendMessage)
    }


    private fun defaultAction(context: MessageContext) {
        val update = context.update()
        val message = update.message
        val source = message.from

        message.webAppData?.let {
            handleSearchOptions(source.id.toString(), it.data)
            silent.send("Successfully sent data from web app to server: ${it.data}", source.id)
            return
        }

        silent.send("Test, new beeeaach-" + ThreadLocalRandom.current().nextInt(), source.id)
    }

    // todo mb use some coroutine-friendly tg bot framework
    private fun handleSearchOptions(userId: String, rawSearchOptions: String) = runBlocking {
        val searchOptions = searchOptionsDeserializer.deserialize(rawSearchOptions)
        userHandlerGateway.setSearchOptions(userId, searchOptions)
    }

    override fun onNewAd(userId: String, flatAd: FlatAd) {
        logger.info("Send ad ${flatAd.id} to $userId")
        // todo move template to file
        val adText = """
            ${flatAd.title}

            ${flatAd.description}

            Price: ${flatAd.price}
            Link: ${flatAd.contacts.flatServiceLink}
        """.trimIndent()

        if (flatAd.images.isEmpty()) {
            sendAdWithoutImages(userId, adText)
            return
        }

        sendAdWithImages(userId, adText, flatAd.images)
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

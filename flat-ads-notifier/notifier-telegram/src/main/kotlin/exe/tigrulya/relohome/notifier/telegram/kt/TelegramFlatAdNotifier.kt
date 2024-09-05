package exe.tigrulya.relohome.notifier.telegram.kt

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.media.sendVisualMediaGroup
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.requests.abstracts.FileId
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.RawChatId
import dev.inmo.tgbotapi.types.media.TelegramMediaPhoto
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import exe.tigrulya.relohome.api.FlatAdKt
import exe.tigrulya.relohome.api.FlatAdNotifierGateway
import exe.tigrulya.relohome.model.Contacts
import exe.tigrulya.relohome.model.FlatAd
import exe.tigrulya.relohome.model.Image
import exe.tigrulya.relohome.notifier.telegram.bot.MessengerLinks
import exe.tigrulya.relohome.template.ObjectReuseMustacheTemplateEngine
import exe.tigrulya.relohome.template.TemplateEngine
import exe.tigrulya.relohome.util.LoggerProperty
import java.net.URLEncoder

class TelegramFlatAdNotifier(
    private val telegramBot: TelegramBot,
    private val templateEngine: TemplateEngine = ObjectReuseMustacheTemplateEngine(),
) : FlatAdNotifierGateway {

    private val logger by LoggerProperty()

    override suspend fun onNewAd(userIds: List<String>, flatAd: FlatAd) {
        logger.info("Send ad ${flatAd.id} to $userIds")

        val flatAdMessage = compileFlatAdMessage(flatAd.withShrinkedDescription())
        if (flatAd.images.isEmpty()) {
            userIds.forEach { userId -> sendAdWithoutImages(userId, flatAdMessage) }
            return
        }

        userIds.forEach { sendAdWithImages(it, flatAdMessage, flatAd.images) }
    }

    private suspend fun sendAdWithoutImages(userId: String, message: String) {
        telegramBot.send(
            chatId = ChatId(RawChatId(userId.toLong())),
            text = message,
            parseMode = MarkdownParseMode
        )
    }

    private suspend fun sendAdWithImages(userId: String, message: String, images: List<Image>) {
        val imagesGroup = images
            .take(10)
            .withIndex()
            .map {
                TelegramMediaPhoto(
                    file = FileId(it.value.url),
                    text = if (it.index == 0) message else null,
                    parseMode = MarkdownParseMode,
                )
            }

        telegramBot.sendVisualMediaGroup(
            chatId = ChatId(RawChatId(userId.toLong())),
            media = imagesGroup
        )
    }

    private fun compileFlatAdMessage(flatAd: FlatAd): String {
        val links = with(flatAd.contacts) {
            MessengerLinks(
                whatsAppLink = messengerIds[Contacts.Messenger.WHATSAPP]?.let {
                    "https://api.whatsapp.com/send/?phone=${it}&text=${
                        URLEncoder.encode(
                            "Hi! I saw your ad on ${flatAd.serviceId}. " +
                                    "Is it still available? ${flatAd.contacts.flatServiceLink}", Charsets.UTF_8
                        )
                    }"
                },
                viberLink = messengerIds[Contacts.Messenger.VIBER]?.let {
                    "viber://chat/?number=${it}"
                }
            )
        }
        val scopes = arrayOf(flatAd, links)
        return templateEngine.compile("templates/new-flat-ad.mustache", *scopes)
    }

    private fun FlatAd.withShrinkedDescription(maxSize: Int = 700) = FlatAd(
        id,
        title,
        address,
        info,
        price,
        description?.maybeShrink(maxSize),
        contacts,
        serviceId,
        images
    )

    private fun String.maybeShrink(maxSize: Int = 700) = if (length >= maxSize) {
        substring(0, maxSize) + "..."
    } else this
}

data class MessengerLinks(val whatsAppLink: String? = null, val viberLink: String? = null)
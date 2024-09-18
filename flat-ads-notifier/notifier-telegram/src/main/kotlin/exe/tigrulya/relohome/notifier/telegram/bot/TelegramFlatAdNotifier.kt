package exe.tigrulya.relohome.notifier.telegram.bot

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.media.sendPhoto
import dev.inmo.tgbotapi.extensions.api.send.media.sendVisualMediaGroup
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.requests.abstracts.FileId
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.RawChatId
import dev.inmo.tgbotapi.types.media.TelegramMediaPhoto
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import exe.tigrulya.relohome.api.FlatAdNotifierGateway
import exe.tigrulya.relohome.model.Contacts
import exe.tigrulya.relohome.model.FlatAd
import exe.tigrulya.relohome.model.Image
import exe.tigrulya.relohome.model.UserInfo
import exe.tigrulya.relohome.template.ObjectReuseMustacheTemplateEngine
import exe.tigrulya.relohome.template.TemplateEngine
import exe.tigrulya.relohome.util.LoggerProperty
import java.net.URLEncoder

class TelegramFlatAdNotifier(
    private val telegramBot: TelegramBot,
    private val keyboardProvider: KeyboardFactory,
    private val templateEngine: TemplateEngine = ObjectReuseMustacheTemplateEngine(),
) : FlatAdNotifierGateway {

    private val logger by LoggerProperty()

    override suspend fun onNewAd(users: List<UserInfo>, flatAd: FlatAd) {
        logger.info("Send ad ${flatAd.id} to ${users.map { it.id }}")

        val fixedFlatAd = flatAd.withShrunkDescription()

        if (flatAd.images.isEmpty()) {
            users.forEach { user -> sendAdWithoutImages(user, fixedFlatAd) }
            return
        }

        users.forEach { sendAdWithImages(it, fixedFlatAd, flatAd.images) }
    }

    private suspend fun sendAdWithoutImages(user: UserInfo, flatAd: FlatAd) {
        val message = compileFlatAdMessage(flatAd, user.locale)

        telegramBot.send(
            chatId = ChatId(RawChatId(user.id.toLong())),
            text = message,
            parseMode = MarkdownParseMode,
            // TODO do we need to recreate keyboards?
            // TODO it doesn't work for some reason
            replyMarkup = keyboardProvider.mainReplyKeyboard(user.id, user.locale, searchEnabled = true)
        )
    }

    private suspend fun sendAdWithImages(user: UserInfo, flatAd: FlatAd, images: List<Image>) {
        val message = compileFlatAdMessage(flatAd, user.locale)

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

        if (imagesGroup.size == 1) {
            imagesGroup.first().run {
                telegramBot.sendPhoto(
                    chatId = ChatId(RawChatId(user.id.toLong())),
                    fileId = file,
                    text = text,
                    parseMode = parseMode,
                )
            }
            return
        }

        telegramBot.sendVisualMediaGroup(
            chatId = ChatId(RawChatId(user.id.toLong())),
            media = imagesGroup
        )
    }

    private fun compileFlatAdMessage(flatAd: FlatAd, locale: String): String {
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
        // todo move templates/ as baseDir option to templateEngine
        return templateEngine.compile("templates/$locale/new-flat-ad.mustache", *scopes)
    }

    // todo also escape markdown special symbols
    // https://stackoverflow.com/questions/61224362/telegram-bot-cant-find-end-of-the-entity-starting-at-truncated
    private fun FlatAd.withShrunkDescription(maxSize: Int = 700) = FlatAd(
        id,
        title,
        address,
        info,
        price,
        description.mapValues { it.value.maybeShrink(maxSize) },
        contacts,
        serviceId,
        images
    )

    private fun String.maybeShrink(maxSize: Int = 700): String = if (length >= maxSize) {
        substring(0, maxSize) + "..."
    } else this
}

data class MessengerLinks(val whatsAppLink: String? = null, val viberLink: String? = null)
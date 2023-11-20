package exe.tigrulya.relohome.notifier.telegram

import com.google.common.util.concurrent.RateLimiter
import org.telegram.abilitybots.api.bot.AbilityBot
import org.telegram.abilitybots.api.toggle.AbilityToggle
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPhoto
import org.telegram.telegrambots.meta.api.methods.send.*
import org.telegram.telegrambots.meta.api.methods.stickers.AddStickerToSet
import org.telegram.telegrambots.meta.api.methods.stickers.CreateNewStickerSet
import org.telegram.telegrambots.meta.api.methods.stickers.SetStickerSetThumb
import org.telegram.telegrambots.meta.api.methods.stickers.UploadStickerFile
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia
import org.telegram.telegrambots.meta.api.objects.File
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.updateshandlers.SentCallback
import java.io.Serializable
import java.util.concurrent.CompletableFuture

abstract class RateLimitingAbilityBot(
    botToken: String,
    botUsername: String,
    toggle: AbilityToggle,
    options: DefaultBotOptions,
    permitsPerSecond: Int = 25
) : AbilityBot(botToken, botUsername, toggle, options) {

    private val rateLimiter = RateLimiter.create(permitsPerSecond.toDouble())

    override fun executeAsync(sendDocument: SendDocument?): CompletableFuture<Message> {
        rateLimiter.acquire()
        return super.executeAsync(sendDocument)
    }

    override fun executeAsync(sendPhoto: SendPhoto?): CompletableFuture<Message> {
        rateLimiter.acquire()
        return super.executeAsync(sendPhoto)
    }

    override fun executeAsync(sendVideo: SendVideo?): CompletableFuture<Message> {
        rateLimiter.acquire()
        return super.executeAsync(sendVideo)
    }

    override fun executeAsync(sendVideoNote: SendVideoNote?): CompletableFuture<Message> {
        rateLimiter.acquire()
        return super.executeAsync(sendVideoNote)
    }

    override fun executeAsync(sendSticker: SendSticker?): CompletableFuture<Message> {
        rateLimiter.acquire()
        return super.executeAsync(sendSticker)
    }

    override fun executeAsync(sendAudio: SendAudio?): CompletableFuture<Message> {
        rateLimiter.acquire()
        return super.executeAsync(sendAudio)
    }

    override fun executeAsync(sendVoice: SendVoice?): CompletableFuture<Message> {
        rateLimiter.acquire()
        return super.executeAsync(sendVoice)
    }

    override fun executeAsync(sendMediaGroup: SendMediaGroup?): CompletableFuture<MutableList<Message>> {
        rateLimiter.acquire()
        return super.executeAsync(sendMediaGroup)
    }

    override fun executeAsync(setChatPhoto: SetChatPhoto?): CompletableFuture<Boolean> {
        rateLimiter.acquire()
        return super.executeAsync(setChatPhoto)
    }

    override fun executeAsync(addStickerToSet: AddStickerToSet?): CompletableFuture<Boolean> {
        rateLimiter.acquire()
        return super.executeAsync(addStickerToSet)
    }

    override fun executeAsync(setStickerSetThumb: SetStickerSetThumb?): CompletableFuture<Boolean> {
        rateLimiter.acquire()
        return super.executeAsync(setStickerSetThumb)
    }

    override fun executeAsync(createNewStickerSet: CreateNewStickerSet?): CompletableFuture<Boolean> {
        rateLimiter.acquire()
        return super.executeAsync(createNewStickerSet)
    }

    override fun executeAsync(uploadStickerFile: UploadStickerFile?): CompletableFuture<File> {
        rateLimiter.acquire()
        return super.executeAsync(uploadStickerFile)
    }

    override fun executeAsync(editMessageMedia: EditMessageMedia?): CompletableFuture<Serializable> {
        rateLimiter.acquire()
        return super.executeAsync(editMessageMedia)
    }

    override fun executeAsync(sendAnimation: SendAnimation?): CompletableFuture<Message> {
        rateLimiter.acquire()
        return super.executeAsync(sendAnimation)
    }

    override fun <T : Serializable?, Method : BotApiMethod<T>?, Callback : SentCallback<T>?> executeAsync(
        method: Method,
        callback: Callback
    ) {
        rateLimiter.acquire()
        super.executeAsync(method, callback)
    }

    override fun <T : Serializable?, Method : BotApiMethod<T>?> executeAsync(method: Method): CompletableFuture<T> {
        rateLimiter.acquire()
        return super.executeAsync(method)
    }

    override fun execute(setChatPhoto: SetChatPhoto?): Boolean {
        rateLimiter.acquire()
        return super.execute(setChatPhoto)
    }

    override fun execute(sendMediaGroup: SendMediaGroup?): MutableList<Message> {
        rateLimiter.acquire()
        return super.execute(sendMediaGroup)
    }

    override fun execute(addStickerToSet: AddStickerToSet?): Boolean {
        rateLimiter.acquire()
        return super.execute(addStickerToSet)
    }

    override fun execute(setStickerSetThumb: SetStickerSetThumb?): Boolean {
        rateLimiter.acquire()
        return super.execute(setStickerSetThumb)
    }

    override fun execute(createNewStickerSet: CreateNewStickerSet?): Boolean {
        rateLimiter.acquire()
        return super.execute(createNewStickerSet)
    }

    override fun execute(uploadStickerFile: UploadStickerFile?): File {
        rateLimiter.acquire()
        return super.execute(uploadStickerFile)
    }

    override fun execute(editMessageMedia: EditMessageMedia?): Serializable {
        rateLimiter.acquire()
        return super.execute(editMessageMedia)
    }

    override fun execute(sendAnimation: SendAnimation?): Message {
        rateLimiter.acquire()
        return super.execute(sendAnimation)
    }

    override fun <T : Serializable?, Method : BotApiMethod<T>?> execute(method: Method): T {
        rateLimiter.acquire()
        return super.execute(method)
    }
}
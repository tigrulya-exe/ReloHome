package exe.tigrulya.relohome.notifier.telegram.bot

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

// todo replace inheritance with withRateLimiter{Async} wrappers
abstract class RateLimitingAbilityBot(
    botToken: String,
    botUsername: String,
    toggle: AbilityToggle,
    options: DefaultBotOptions,
    permitsPerSecond: Int = 15
) : AbilityBot(botToken, botUsername, toggle, options) {

    private val rateLimiter = RateLimiter.create(permitsPerSecond.toDouble())

    private fun <T> withRateLimiterAsync(block: () -> CompletableFuture<T>): CompletableFuture<T> {
        return CompletableFuture.runAsync({ rateLimiter.acquire() }, exe)
            .thenCompose { block.invoke() }
    }

    private fun <T> withRateLimiter(block: () -> T): T {
        rateLimiter.acquire()
        return block.invoke()
    }

    override fun executeAsync(sendDocument: SendDocument?) = withRateLimiterAsync {
        super.executeAsync(sendDocument)
    }

    override fun executeAsync(sendPhoto: SendPhoto?) = withRateLimiterAsync {
        super.executeAsync(sendPhoto)
    }

    override fun executeAsync(sendVideo: SendVideo?) = withRateLimiterAsync {
        super.executeAsync(sendVideo)
    }

    override fun executeAsync(sendVideoNote: SendVideoNote?) = withRateLimiterAsync {
        super.executeAsync(sendVideoNote)
    }

    override fun executeAsync(sendSticker: SendSticker?) = withRateLimiterAsync {
        super.executeAsync(sendSticker)
    }

    override fun executeAsync(sendAudio: SendAudio?) = withRateLimiterAsync {
        super.executeAsync(sendAudio)
    }

    override fun executeAsync(sendVoice: SendVoice?) = withRateLimiterAsync {
        super.executeAsync(sendVoice)
    }

    override fun executeAsync(sendMediaGroup: SendMediaGroup?) = withRateLimiterAsync {
        super.executeAsync(sendMediaGroup)
    }

    override fun executeAsync(setChatPhoto: SetChatPhoto?) = withRateLimiterAsync {
        super.executeAsync(setChatPhoto)
    }

    override fun executeAsync(addStickerToSet: AddStickerToSet?) = withRateLimiterAsync {
        super.executeAsync(addStickerToSet)
    }

    override fun executeAsync(setStickerSetThumb: SetStickerSetThumb?) = withRateLimiterAsync {
        super.executeAsync(setStickerSetThumb)
    }

    override fun executeAsync(createNewStickerSet: CreateNewStickerSet?) = withRateLimiterAsync {
        super.executeAsync(createNewStickerSet)
    }

    override fun executeAsync(uploadStickerFile: UploadStickerFile?) = withRateLimiterAsync {
        super.executeAsync(uploadStickerFile)
    }

    override fun executeAsync(editMessageMedia: EditMessageMedia?) = withRateLimiterAsync {
        super.executeAsync(editMessageMedia)
    }

    override fun executeAsync(sendAnimation: SendAnimation?) = withRateLimiterAsync {
        super.executeAsync(sendAnimation)
    }

    override fun <T : Serializable?, Method : BotApiMethod<T>?, Callback : SentCallback<T>?> executeAsync(
        method: Method,
        callback: Callback
    ) = withRateLimiter {
        super.executeAsync(method, callback)
    }

    override fun <T : Serializable?, Method : BotApiMethod<T>?> executeAsync(method: Method) = withRateLimiterAsync {
        super.executeAsync(method)
    }

    override fun execute(setChatPhoto: SetChatPhoto?): Boolean = withRateLimiter {
        super.execute(setChatPhoto)
    }

    override fun execute(sendMediaGroup: SendMediaGroup?): MutableList<Message> = withRateLimiter {
        super.execute(sendMediaGroup)
    }

    override fun execute(addStickerToSet: AddStickerToSet?): Boolean = withRateLimiter {
        super.execute(addStickerToSet)
    }

    override fun execute(setStickerSetThumb: SetStickerSetThumb?): Boolean = withRateLimiter {
        super.execute(setStickerSetThumb)
    }

    override fun execute(createNewStickerSet: CreateNewStickerSet?): Boolean = withRateLimiter {
        super.execute(createNewStickerSet)
    }

    override fun execute(uploadStickerFile: UploadStickerFile?): File = withRateLimiter {
        super.execute(uploadStickerFile)
    }

    override fun execute(editMessageMedia: EditMessageMedia?): Serializable = withRateLimiter {
        super.execute(editMessageMedia)
    }

    override fun execute(sendAnimation: SendAnimation?): Message = withRateLimiter {
        super.execute(sendAnimation)
    }

    override fun <T : Serializable?, Method : BotApiMethod<T>?> execute(method: Method): T = withRateLimiter {
        super.execute(method)
    }
}
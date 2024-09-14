package exe.tigrulya.relohome.notifier.telegram.bot.state


interface LocalizationContext {
    suspend fun constant(id: String): String

    suspend fun <T> constant(id: String, ctx: T): String
}

interface LocalizationManager {

    suspend fun <T> withLocalization(userId: String, action: suspend LocalizationContext.() -> T): T
}
package exe.tigrulya.relohome.notifier.telegram.bot.state


interface LocalizationContext {
    val locale: String

    suspend fun constant(id: String): String

    suspend fun <T> constant(id: String, ctx: T): String
}

interface LocalizationManager {

    suspend fun <T> withLocalization(userId: String, action: suspend LocalizationContext.() -> T): T

    suspend fun setLocale(userId: String, locale: String)

    suspend fun constant(userId: String, id: String): String

    suspend fun <T> constant(userId: String, id: String, ctx: T): String
}
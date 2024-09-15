package exe.tigrulya.relohome.notifier.telegram.bot.state

import exe.tigrulya.relohome.localization.Localization
import exe.tigrulya.relohome.notifier.telegram.bot.state.repo.InMemoryUserLocalesRepository
import exe.tigrulya.relohome.notifier.telegram.bot.state.repo.UserLocalesRepository

class DefaultLocalizationContext(
    private val locale: String,
    private val localization: Localization
) : LocalizationContext {
    override suspend fun constant(id: String): String =
        localization[locale, id]?.rawValue
            ?: throw IllegalArgumentException("Wrong constant id: $id")

    override suspend fun <T> constant(id: String, ctx: T): String =
        localization[locale, id]
            ?.eval(ctx)
            ?: throw IllegalArgumentException("Wrong constant id: $id")

}

class DefaultLocalizationManager(
    private val localization: Localization,
    private val userLocalesRepo: UserLocalesRepository = InMemoryUserLocalesRepository(),
    private val defaultLocale: String = "en"
) : LocalizationManager {

    override suspend fun <T> withLocalization(userId: String, action: suspend (LocalizationContext) -> T): T {
        val locale = userLocalesRepo.get(userId) ?: defaultLocale
        return action.invoke(DefaultLocalizationContext(locale, localization))
    }

    override suspend fun setLocale(userId: String, locale: String) {
        userLocalesRepo.set(userId, locale)
    }

    override suspend fun constant(userId: String, id: String): String {
        val locale = userLocalesRepo.get(userId) ?: defaultLocale
        return DefaultLocalizationContext(locale, localization).constant(id)
    }

    override suspend fun <T> constant(userId: String, id: String, ctx: T): String {
        val locale = userLocalesRepo.get(userId) ?: defaultLocale
        return DefaultLocalizationContext(locale, localization).constant(id, ctx)
    }
}
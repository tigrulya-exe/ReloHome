package exe.tigrulya.relohome.notifier.telegram.bot.state.repo

import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap

interface UserLocalesRepository {

    suspend fun getLocale(userId: String): String?

    suspend fun setLocale(userId: String, locale: String)
}

class InMemoryUserLocalesRepository : UserLocalesRepository {
    private val locales: ConcurrentHashMap<String, String> = ConcurrentHashMap()

    override suspend fun getLocale(userId: String) = locales[userId]

    override suspend fun setLocale(userId: String, locale: String) {
        locales[userId] = locale
    }
}
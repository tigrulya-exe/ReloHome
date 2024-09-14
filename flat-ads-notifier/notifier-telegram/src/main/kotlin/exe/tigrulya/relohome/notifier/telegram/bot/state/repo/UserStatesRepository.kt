package exe.tigrulya.relohome.notifier.telegram.bot.state.repo

import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap

enum class UserState {
    NEW,
    LOCALE_PROVIDED,
    SEARCH_OPTIONS_PROVIDED
}

interface UserStatesRepository {

    suspend fun get(userId: String): UserState?

    suspend fun set(userId: String, state: UserState)

}

class InMemoryUserStatesRepository : UserStatesRepository {

    private val states: ConcurrentHashMap<String, UserState> = ConcurrentHashMap()

    override suspend fun get(userId: String): UserState? {
        return states[userId]
    }

    override suspend fun set(userId: String, state: UserState) {
        states[userId] = state
    }
}

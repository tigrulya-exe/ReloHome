package exe.tigrulya.relohome.notifier.telegram.bot.state.repo

import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap

enum class UserState {
    NEW,
    LOCALE_PROVIDED,
    SEARCH_OPTIONS_PROVIDED
}

interface UserStatesRepository {

    suspend fun getState(userId: String): UserState?

    suspend fun setState(userId: String, state: UserState)

}

class InMemoryUserStatesRepository : UserStatesRepository {

    private val states: ConcurrentHashMap<String, UserState> = ConcurrentHashMap()

    override suspend fun getState(userId: String): UserState? {
        return states[userId]
    }

    override suspend fun setState(userId: String, state: UserState) {
        states[userId] = state
    }
}

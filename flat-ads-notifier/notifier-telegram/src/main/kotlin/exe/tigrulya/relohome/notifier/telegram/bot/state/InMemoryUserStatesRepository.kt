package exe.tigrulya.relohome.notifier.telegram.bot.state

import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap

class InMemoryUserStatesRepository : UserStatesRepository {

    private val states: ConcurrentHashMap<String, UserState> = ConcurrentHashMap()

    override suspend fun get(userId: String): UserState? {
        return states[userId]
    }

    override suspend fun set(userId: String, state: UserState) {
        states[userId] = state
    }
}

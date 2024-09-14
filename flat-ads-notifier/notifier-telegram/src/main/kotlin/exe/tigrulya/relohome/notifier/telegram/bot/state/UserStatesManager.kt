package exe.tigrulya.relohome.notifier.telegram.bot.state

enum class UserState {
    NEW,
    LOCALE_PROVIDED,
    SEARCH_OPTIONS_PROVIDED
}

interface UserStatesRepository {

    suspend fun get(userId: String): UserState?

    suspend fun set(userId: String, state: UserState)

}

interface UserStatesManager {
    suspend fun <T> onlyIfNoState(userId: String, handler: suspend UserStatesManager.() -> T): T?

    suspend fun <T> onlyOnState(userId: String, state: UserState, handler: suspend UserStatesManager.() -> T): T?

    suspend fun <T> onlyOnStates(userId: String, vararg states: UserState, handler: suspend UserStatesManager.(UserState) -> T): T?

    suspend fun transition(userId: String, state: UserState)
}
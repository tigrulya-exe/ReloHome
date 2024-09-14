package exe.tigrulya.relohome.notifier.telegram.bot.state

import exe.tigrulya.relohome.notifier.telegram.bot.state.repo.UserState


interface UserStatesManager {
    suspend fun <T> onlyIfNoState(userId: String, handler: suspend UserStatesManager.() -> T): T?

    suspend fun <T> onlyOnState(userId: String, state: UserState, handler: suspend UserStatesManager.() -> T): T?

    suspend fun <T> onlyOnStates(userId: String, vararg states: UserState, handler: suspend UserStatesManager.(UserState) -> T): T?

    suspend fun transition(userId: String, state: UserState)
}
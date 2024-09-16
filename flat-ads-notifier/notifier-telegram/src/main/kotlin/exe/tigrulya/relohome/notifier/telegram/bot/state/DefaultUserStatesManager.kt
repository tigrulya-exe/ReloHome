package exe.tigrulya.relohome.notifier.telegram.bot.state

import exe.tigrulya.relohome.notifier.telegram.bot.state.repo.UserState
import exe.tigrulya.relohome.notifier.telegram.bot.state.repo.UserStatesRepository


class DefaultUserStatesManager(
    private val repository: UserStatesRepository
) : UserStatesManager {
    override suspend fun <T> onlyIfNoState(
        userId: String,
        handler: suspend UserStatesManager.() -> T
    ): T? = if (repository.getState(userId) == null) {
        handler.invoke(this)
    } else null

    override suspend fun <T> onlyOnState(
        userId: String,
        state: UserState,
        handler: suspend UserStatesManager.() -> T
    ): T? = repository.getState(userId)
        ?.takeIf { it == state }
        ?.let { return handler.invoke(this) }

    override suspend fun <T> onlyOnStates(
        userId: String,
        vararg states: UserState,
        handler: suspend UserStatesManager.(UserState) -> T
    ) = repository.getState(userId)
        ?.takeIf { state -> states.any { state == it } }
        ?.let { handler.invoke(this, it) }

    override suspend fun transition(userId: String, state: UserState) {
        repository.setState(userId, state)
    }
}
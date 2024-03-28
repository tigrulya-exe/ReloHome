package exe.tigrulya.relohome.api.user_handler

import exe.tigrulya.relohome.model.City
import exe.tigrulya.relohome.model.UserCreateDto
import exe.tigrulya.relohome.model.UserSearchOptionsDto
import io.grpc.Status
import io.grpc.StatusException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

class BlockingUserHandlerGateway(
    private val delegate: UserHandlerGateway,
    private val context: CoroutineContext
) {
    companion object {
        private val ThrowingErrorCallback: ErrorCallback<Nothing> = { throw it }

        fun wrap(
            userHandlerGateway: UserHandlerGateway,
            context: CoroutineContext = Dispatchers.Default
        ) = BlockingUserHandlerGateway(userHandlerGateway, context)
    }

    fun registerUser(
        user: UserCreateDto
    ): Result<Unit> = runBlocking {
        runCatchingStatusError { delegate.registerUser(user) }
    }

    fun setLocation(
        externalId: String,
        city: City,
    ): Result<Unit> = runBlocking {
        runCatchingStatusError { delegate.setLocation(externalId, city) }
    }

    fun setSearchOptions(
        externalId: String,
        searchOptions: UserSearchOptionsDto,
    ): Result<Unit> = runBlocking {
        runCatchingStatusError { delegate.setSearchOptions(externalId, searchOptions) }
    }

    fun toggleSearch(
        externalId: String
    ): Result<Boolean> = runBlocking(context) {
        runCatchingStatusError { delegate.toggleSearch(externalId) }
    }

    private inline fun <T, R> T.runCatchingStatusError(block: T.() -> R): Result<R> {
        return try {
            Result.success(block())
        } catch (e: StatusException) {
            // todo
            val message = if (e.status == Status.FAILED_PRECONDITION) {
                "Request error: ${e.message?.replace("FAILED_PRECONDITION:", "")}"
            } else {
                "Server error: ${
                    e.message?.replace(
                        "INTERNAL:",
                        ""
                    )
                }. Please, contact /support and describe your problem."
            }
            Result.failure(RuntimeException(message))
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}

fun UserHandlerGateway.blocking(): BlockingUserHandlerGateway = BlockingUserHandlerGateway.wrap(this)

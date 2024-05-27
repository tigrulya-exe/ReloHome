package exe.tigrulya.relohome.api.user_handler

import exe.tigrulya.relohome.error.ReloHomeException
import exe.tigrulya.relohome.error.ReloHomeServerException
import exe.tigrulya.relohome.error.ReloHomeUserException
import exe.tigrulya.relohome.model.City
import exe.tigrulya.relohome.model.UserCreateDto
import exe.tigrulya.relohome.model.UserSearchOptionsDto
import io.grpc.Status
import io.grpc.StatusException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import kotlin.coroutines.CoroutineContext

class AsyncUserHandlerGateway(
    private val delegate: UserHandlerGateway,
    context: CoroutineContext = Dispatchers.IO,
) {
    private val supervisor = SupervisorJob()
    private val coroutineScope: CoroutineScope = CoroutineScope(context + supervisor)

    fun registerUser(
        user: UserCreateDto
    ): CompletableFuture<Unit> = errorHandlingFuture {
        delegate.registerUser(user)
    }

    fun setLocation(
        externalId: String,
        city: City,
    ): CompletableFuture<Unit> = errorHandlingFuture {
        delegate.setLocation(externalId, city)
    }

    fun setSearchOptions(
        externalId: String,
        searchOptions: UserSearchOptionsDto,
    ): CompletableFuture<Unit> = errorHandlingFuture {
        delegate.setSearchOptions(externalId, searchOptions)
    }

    fun toggleSearch(
        externalId: String
    ): CompletableFuture<Boolean> {
        return errorHandlingFuture {
            delegate.toggleSearch(externalId)
        }
    }

    private fun <T> errorHandlingFuture(
        block: suspend CoroutineScope.() -> T
    ): CompletableFuture<T> {
        return coroutineScope.future {
            block.invoke(this)
        }.handle { result, error ->
            error ?: return@handle result

            val message = when (error.unwrap()) {
                is ReloHomeUserException -> "Request error: ${error.realMessage}"
                is ReloHomeServerException -> "Server error: ${error.realMessage}. " +
                        "Please, contact /support and describe your problem."
                is StatusException -> {
                    if ((error as StatusException).status == Status.FAILED_PRECONDITION) {
                        "Request error: ${error.realMessage?.replace("FAILED_PRECONDITION:", "")}"
                    } else {
                        "Server error: ${
                            error.realMessage?.replace(
                                "INTERNAL:",
                                ""
                            )
                        }. Please, contact /support and describe your problem."
                    }
                }
                else -> "Unknown error. Please, contact /support and describe your problem"
            }
            throw ReloHomeException(message)
        }
    }
}

fun UserHandlerGateway.async(coroutineContext: CoroutineContext = Dispatchers.IO): AsyncUserHandlerGateway =
    AsyncUserHandlerGateway(this, coroutineContext)

fun Throwable.unwrap(): Throwable = when(this) {
    is CancellationException -> cause!!
    else -> this
}

val Throwable.realMessage: String?
    get() = when(this) {
        is CancellationException -> cause?.message
        is CompletionException -> cause?.message
        else -> message
    }

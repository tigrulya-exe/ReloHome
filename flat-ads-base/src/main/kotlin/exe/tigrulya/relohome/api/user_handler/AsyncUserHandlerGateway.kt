package exe.tigrulya.relohome.api.user_handler

import exe.tigrulya.relohome.error.ReloHomeException
import exe.tigrulya.relohome.error.ReloHomeServerException
import exe.tigrulya.relohome.error.ReloHomeUserException
import exe.tigrulya.relohome.model.City
import exe.tigrulya.relohome.model.UserCreateDto
import exe.tigrulya.relohome.model.UserSearchOptionsDto
import io.grpc.Status
import io.grpc.StatusException
import kotlinx.coroutines.*
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.CoroutineContext

class AsyncUserHandlerGateway(
    private val delegate: UserHandlerGateway,
    private val context: CoroutineContext
) {
    fun registerUser(
        user: UserCreateDto
    ): CompletableFuture<Unit> = runBlocking(context) {
        errorHandlingFuture {
            delegate.registerUser(user)
        }
    }

    fun setLocation(
        externalId: String,
        city: City,
    ): CompletableFuture<Unit> = runBlocking(context) {
        errorHandlingFuture {
            delegate.setLocation(externalId, city)
        }
    }

    fun setSearchOptions(
        externalId: String,
        searchOptions: UserSearchOptionsDto,
    ): CompletableFuture<Unit> = runBlocking(context) {
        errorHandlingFuture {
            delegate.setSearchOptions(externalId, searchOptions)
        }
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
        // TODO instead of GlobalScope provide smth like BotScope
        return GlobalScope.future {
            block.invoke(this)
        }.handle { result, error ->
            error ?: return@handle result

            val message = when (error) {
                is ReloHomeUserException -> "Request error: ${error.message}"
                is ReloHomeServerException -> "Server error: ${error.message}. " +
                        "Please, contact /support and describe your problem."
                is StatusException -> {
                    if (error.status == Status.FAILED_PRECONDITION) {
                        "Request error: ${error.message?.replace("FAILED_PRECONDITION:", "")}"
                    } else {
                        "Server error: ${
                            error.message?.replace(
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

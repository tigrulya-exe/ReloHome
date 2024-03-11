package exe.tigrulya.relohome.error

import exe.tigrulya.relohome.util.LoggerProperty
import io.grpc.StatusException

interface WithGrpcClientErrorHandling {
    companion object {
        val loggerProperty by LoggerProperty()
    }

    fun <R> handleClientError(clientException: ReloHomeUserException, actionName: String): Result<R> {
        loggerProperty.error("Client error during {}", actionName, clientException)
        return Result.failure(clientException)
    }

    fun <R> handleServerError(serverException: ReloHomeServerException, actionName: String): Result<R> {
        loggerProperty.error("Unexpected server exception during {}", actionName, serverException)
        return Result.failure(serverException)
    }

    fun <R> handleOtherError(otherException: Throwable, actionName: String): Result<R> {
        loggerProperty.error("Unexpected exception during {}", actionName, otherException)
        return Result.failure(otherException)
    }

    suspend fun <R> withSilentErrorHandling(actionName: String = "unnamed action", action: suspend () -> R): Result<R> {
        return try {
            Result.success(action.invoke())
        } catch (statusException: StatusException) {
            return when (val cause = statusException.cause) {
                is ReloHomeServerException -> handleServerError(cause, actionName)
                is ReloHomeUserException -> handleClientError(cause, actionName)
                else -> cause?.let { handleOtherError(it, actionName) }
                    ?: handleOtherError(statusException, actionName)
            }
        } catch (otherException: Exception) {
            handleOtherError(otherException, actionName)
        }
    }

    suspend fun withErrorHandling(actionName: String = "unnamed action", action: suspend () -> Unit) {
        withSilentErrorHandling(actionName, action)
    }
}


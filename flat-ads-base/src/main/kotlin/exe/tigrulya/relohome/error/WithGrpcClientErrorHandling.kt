package exe.tigrulya.relohome.error

import exe.tigrulya.relohome.util.LoggerProperty

// TODO IMPLEMENT IT
interface WithGrpcClientErrorHandling {
    companion object {
        val loggerProperty by LoggerProperty()
    }

    fun <R> handleClientError(clientException: ReloHomeClientException, actionName: String = "unnamed action"): R {
        loggerProperty.error("Client error during {}", actionName, clientException)
        throw clientException
    }

    fun <R> handleServerError(serverException: ReloHomeServerException, actionName: String = "unnamed action"): R {
        loggerProperty.error("Unexpected server exception during {}", actionName, serverException)
        throw serverException
    }

    fun <R> handleOtherError(otherException: Exception, actionName: String = "unnamed action"): R {
        loggerProperty.error("Unexpected exception during {}", actionName, otherException)
        throw otherException
    }

    fun <R> withErrorHandling(actionName: String = "unnamed action", action: () -> R): R {
        return try {
            action.invoke()
        } catch (clientException: ReloHomeClientException) {
            handleClientError(clientException)
        } catch (serverException: ReloHomeServerException) {
            handleServerError(serverException)
        } catch (otherException: Exception) {
            handleOtherError(otherException)
        }
    }
}
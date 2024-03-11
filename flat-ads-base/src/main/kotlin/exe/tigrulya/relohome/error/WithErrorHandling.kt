package exe.tigrulya.relohome.error

import exe.tigrulya.relohome.util.LoggerProperty

interface WithErrorHandling {
    companion object {
        val loggerProperty by LoggerProperty()
    }

    fun <R> handleClientError(clientException: ReloHomeUserException, actionName: String): R {
        loggerProperty.error("Client error during {}", actionName, clientException)
        throw clientException
    }

    fun <R> handleServerError(serverException: ReloHomeServerException, actionName: String): R {
        loggerProperty.error("Unexpected server exception during {}", actionName, serverException)
        throw serverException
    }

    fun <R> handleOtherError(otherException: Exception, actionName: String): R {
        loggerProperty.error("Unexpected exception during {}", actionName, otherException)
        throw otherException
    }

    fun <R> withErrorHandling(actionName: String = "unnamed action", action: () -> R): R {
        return try {
            action.invoke()
        } catch (clientException: ReloHomeUserException) {
            handleClientError(clientException, actionName)
        } catch (serverException: ReloHomeServerException) {
            handleServerError(serverException, actionName)
        } catch (otherException: Exception) {
            handleOtherError(otherException, actionName)
        }
    }
}


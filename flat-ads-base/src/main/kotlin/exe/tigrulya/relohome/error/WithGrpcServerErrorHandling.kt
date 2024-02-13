package exe.tigrulya.relohome.error

import io.grpc.Status.FAILED_PRECONDITION
import io.grpc.Status.INTERNAL
import io.grpc.StatusException

interface WithGrpcServerErrorHandling: WithErrorHandling {
    override fun <R> handleClientError(clientException: ReloHomeClientException, actionName: String): R {
        WithErrorHandling.loggerProperty.error("Client error during {}", actionName, clientException)

        val status = FAILED_PRECONDITION
            .withDescription(clientException.localizedMessage)
            .withCause(clientException)
        throw StatusException(status)
    }

    override fun <R> handleServerError(serverException: ReloHomeServerException, actionName: String): R {
        WithErrorHandling.loggerProperty.error("Unexpected server exception during {}", actionName, serverException)
        
        val status = INTERNAL
            .withDescription(serverException.localizedMessage)
            .withCause(serverException)
        throw StatusException(status)
    }

    override fun <R> handleOtherError(otherException: Exception, actionName: String): R {
        WithErrorHandling.loggerProperty.error("Unexpected exception during {}", actionName, otherException)
        
        val status = INTERNAL
            .withDescription(otherException.localizedMessage)
            .withCause(otherException)
        throw StatusException(status)
    }
}
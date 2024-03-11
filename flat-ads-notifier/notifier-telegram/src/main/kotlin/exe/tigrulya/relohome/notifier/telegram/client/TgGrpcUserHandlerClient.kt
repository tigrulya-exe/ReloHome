package exe.tigrulya.relohome.notifier.telegram.client


import exe.tigrulya.relohome.api.grpc.GrpcUserHandlerClient
import exe.tigrulya.relohome.error.ReloHomeUserException
import exe.tigrulya.relohome.error.ReloHomeServerException

class TgGrpcUserHandlerClient(serverUrl: String) : GrpcUserHandlerClient(serverUrl) {
    override fun <R> handleClientError(clientException: ReloHomeUserException, actionName: String): Result<R> {
        return super.handleClientError(clientException, actionName)
    }

    override fun <R> handleServerError(serverException: ReloHomeServerException, actionName: String): Result<R> {
        return super.handleServerError(serverException, actionName)
    }

    override fun <R> handleOtherError(otherException: Throwable, actionName: String): Result<R> {
        return super.handleOtherError(otherException, actionName)
    }
}
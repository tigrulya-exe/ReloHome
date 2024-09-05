package exe.tigrulya.relohome.notifier.telegram.kt.ext


import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.CustomBehaviourContextAndTypeReceiver
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onText
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onWebAppData
import dev.inmo.tgbotapi.types.ChatIdentifier
import dev.inmo.tgbotapi.types.message.ChatEvents.WebAppData
import dev.inmo.tgbotapi.types.message.PrivateEventMessage
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextMessage
import exe.tigrulya.relohome.error.ReloHomeUserException

suspend fun <BC : BehaviourContext> BC.onCommandWithErrorHandling(
    command: String,
    scenarioReceiver: CustomBehaviourContextAndTypeReceiver<BC, Unit, TextMessage>
) = onCommand(
    command = command,
    scenarioReceiver = withEnabledErrorHandling(scenarioReceiver)
)

suspend fun <BC : BehaviourContext> BC.onWebappWithErrorHandling(
    scenarioReceiver: CustomBehaviourContextAndTypeReceiver<BC, Unit, PrivateEventMessage<WebAppData>>
) = onWebAppData(
    scenarioReceiver = withEnabledErrorHandling(scenarioReceiver)
)

suspend fun <BC : BehaviourContext> BC.onTextStartingWith(
    token: String,
    scenarioReceiver: CustomBehaviourContextAndTypeReceiver<BC, Unit, TextMessage>
) = onText(
    initialFilter = { message -> message.content.text.startsWith(token) },
    scenarioReceiver = withEnabledErrorHandling(scenarioReceiver)
)

suspend fun <BC : BehaviourContext> BC.onTextStartingWith(
    vararg tokens: String,
    scenarioReceiver: CustomBehaviourContextAndTypeReceiver<BC, Unit, TextMessage>
) = onText(
    initialFilter = { message ->
        tokens.any { token ->
            message.content.text.startsWith(token)
        }
    },
    scenarioReceiver = withEnabledErrorHandling(scenarioReceiver)
)

fun <BC : BehaviourContext, T> withEnabledErrorHandling(
    scenarioReceiver: CustomBehaviourContextAndTypeReceiver<BC, Unit, T>
): CustomBehaviourContextAndTypeReceiver<BC, Unit, T> = {
    enableErrorHandling {
        scenarioReceiver.invoke(this, it)
    }
}

suspend fun <BC : BehaviourContext> BC.enableErrorHandling(
    scenarioReceiver: suspend BC.() -> Unit
) {
    try {
        scenarioReceiver.invoke(this)
    } catch (stopException: StopHandlingException) {
        // do nothing
    }
}

suspend fun <BC : BehaviourContext> BC.clientError(chatId: ChatIdentifier, errorMessage: String) {
    send(chatId, "Error handling request: $errorMessage")
}

suspend fun <BC : BehaviourContext> BC.serverError(chatId: ChatIdentifier, errorMessage: String) {
    send(chatId, "Internal error. Please contact support: $errorMessage")
}

suspend fun <BC : BehaviourContext, R> BC.withSimpleErrorHandling(
    message: CommonMessage<*>,
    errorText: String = "Wrong request",
    includeExceptionMessage: Boolean = true,
    exceptionMessageGetter: (Exception) -> String = Exception::getLocalizedMessage,
    action: suspend BC.() -> R
): R {
    return withSimpleErrorHandling(
        chatId = message.senderId(),
        errorText = errorText,
        includeExceptionMessage = includeExceptionMessage,
        exceptionMessageGetter = exceptionMessageGetter,
        action = action
    )
}

suspend fun <BC : BehaviourContext, R> BC.withSimpleErrorHandling(
    chatId: ChatIdentifier,
    errorText: String = "Wrong request",
    includeExceptionMessage: Boolean = true,
    exceptionMessageGetter: (Exception) -> String = Exception::getLocalizedMessage,
    action: suspend BC.() -> R
): R {
    return withErrorHandling(
        chatId = chatId,
        clientErrorPrefix = errorText,
        serverErrorPrefix = errorText,
        includeExceptionMessage = includeExceptionMessage,
        exceptionMessageGetter = exceptionMessageGetter,
        action = action
    )
}

suspend fun <BC : BehaviourContext, R> BC.withErrorHandling(
    chatId: ChatIdentifier,
    clientErrorPrefix: String = "Wrong request",
    serverErrorPrefix: String = "Server error",
    includeExceptionMessage: Boolean = true,
    exceptionMessageGetter: (Exception) -> String = Exception::getLocalizedMessage,
    action: suspend BC.() -> R
): R {
    return try {
        action.invoke(this)
    } catch (exception: ReloHomeUserException) {
        val errorMessage = if (includeExceptionMessage) {
            clientErrorPrefix + exceptionMessageGetter(exception)
        } else {
            clientErrorPrefix
        }
        clientError(chatId, errorMessage)
        throw StopHandlingException()
    } catch (exception: Exception) {
        val errorMessage = if (includeExceptionMessage) {
            serverErrorPrefix + exceptionMessageGetter(exception)
        } else {
            serverErrorPrefix
        }
        serverError(chatId, errorMessage)
        throw StopHandlingException()
    }
}

class StopHandlingException : RuntimeException()
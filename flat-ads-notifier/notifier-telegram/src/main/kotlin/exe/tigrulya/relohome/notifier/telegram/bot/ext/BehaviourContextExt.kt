package exe.tigrulya.relohome.notifier.telegram.bot.ext


import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.CustomBehaviourContextAndTypeReceiver
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.*
import dev.inmo.tgbotapi.extensions.behaviour_builder.utils.plus
import dev.inmo.tgbotapi.extensions.utils.botCommandTextSourceOrNull
import dev.inmo.tgbotapi.extensions.utils.fromUserOrThrow
import dev.inmo.tgbotapi.types.ChatIdentifier
import dev.inmo.tgbotapi.types.chat.User
import dev.inmo.tgbotapi.types.message.ChatEvents.WebAppData
import dev.inmo.tgbotapi.types.message.PrivateEventMessage
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.abstracts.Message
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.message.content.TextMessage
import dev.inmo.tgbotapi.types.queries.callback.MessageDataCallbackQuery
import exe.tigrulya.relohome.error.ReloHomeUserException

internal fun commandFilter(commandRegex: Regex) = CommonMessageFilter<TextContent> { message ->
    val content = message.content
    val textSources = content.textSources
    textSources.any {
        commandRegex.matches(it.botCommandTextSourceOrNull()?.command ?: return@any false)
    }
}

internal fun textStartingWithFilter(token: String) = CommonMessageFilter<TextContent> { message ->
    message.content.text.startsWith(token)
}

suspend fun <BC : BehaviourContext> BC.sender(message: Message): User =
    withSimpleErrorHandling(message.senderId(), "Bot currently works only in private chats") {
        message.fromUserOrThrow().user
    }

suspend fun <BC : BehaviourContext> BC.onCommandOrTextStartingWith(
    command: Regex,
    text: String,
    scenarioReceiver: CustomBehaviourContextAndTypeReceiver<BC, Unit, TextMessage>
) = onText(
    initialFilter = textStartingWithFilter(text) + commandFilter(command),
    scenarioReceiver = withEnabledErrorHandling(scenarioReceiver)
)

suspend fun <BC : BehaviourContext> BC.onCallbackData(
    filter: suspend (String) -> Boolean,
    scenarioReceiver: CustomBehaviourContextAndTypeReceiver<BC, Unit, MessageDataCallbackQuery>
) = onMessageDataCallbackQuery(
    initialFilter = { message -> filter.invoke(message.data) },
    scenarioReceiver = withEnabledErrorHandling(scenarioReceiver)
)

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
    initialFilter = textStartingWithFilter(token),
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
            "$clientErrorPrefix. ${exceptionMessageGetter(exception)}"
        } else {
            clientErrorPrefix
        }
        clientError(chatId, errorMessage)
        throw StopHandlingException()
    } catch (exception: Exception) {
        val errorMessage = if (includeExceptionMessage) {
            "$serverErrorPrefix. ${exceptionMessageGetter(exception)}"
        } else {
            serverErrorPrefix
        }
        serverError(chatId, errorMessage)
        throw StopHandlingException()
    }
}

class StopHandlingException : RuntimeException()
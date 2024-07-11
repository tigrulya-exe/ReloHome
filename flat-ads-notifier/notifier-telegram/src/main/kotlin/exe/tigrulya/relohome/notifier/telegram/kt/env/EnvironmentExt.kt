package exe.tigrulya.relohome.notifier.telegram.kt.env

import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import exe.tigrulya.relohome.error.ReloHomeUserException

val MessageHandlerEnvironment.senderId: String
    get() = message.from?.id?.toString()
        ?: throw IllegalStateException("Can't fetch senderId from message: $message")

val MessageHandlerEnvironment.senderUsername: String
    get() = message.from?.username
        ?: throw IllegalStateException("Can't fetch username from message: $message")

val MessageHandlerEnvironment.chatId: ChatId
    get() = ChatId.fromId(message.chat.id)

fun MessageHandlerEnvironment.reply(message: String, replyMarkup: ReplyMarkup? = null) {
    bot.sendMessage(
        chatId = chatId,
        parseMode = ParseMode.HTML,
        text = message,
        replyMarkup = replyMarkup
    )
}

fun MessageHandlerEnvironment.clientError(errorMessage: String) {
    reply("Error handling request: $errorMessage")
}

fun MessageHandlerEnvironment.serverError(errorMessage: String) {
    reply("Internal error. Please contact support: $errorMessage")
}

suspend fun MessageHandlerEnvironment.enableErrorHandling(
    action: suspend MessageHandlerEnvironment.() -> Unit
) {
    try {
        action.invoke(this)
    } catch (stopException: StopHandlingException) {
        // do nothing
    }
}

suspend fun <T> MessageHandlerEnvironment.withSimpleErrorHandling(
    errorText: String = "Wrong request",
    includeExceptionMessage: Boolean = true,
    exceptionMessageGetter: (Exception) -> String = Exception::getLocalizedMessage,
    action: suspend MessageHandlerEnvironment.() -> T
): T {
    return withErrorHandling(
        clientErrorText = errorText,
        serverErrorText = errorText,
        includeExceptionMessage = includeExceptionMessage,
        exceptionMessageGetter = exceptionMessageGetter,
        action = action
    )
}

suspend fun <T> MessageHandlerEnvironment.withErrorHandling(
    clientErrorText: String = "Wrong request",
    serverErrorText: String = "Server error",
    includeExceptionMessage: Boolean = true,
    exceptionMessageGetter: (Exception) -> String = Exception::getLocalizedMessage,
    action: suspend MessageHandlerEnvironment.() -> T
): T {

    return try {
        action.invoke(this)
    } catch (exception: ReloHomeUserException) {
        val message = if (includeExceptionMessage) {
            clientErrorText + exceptionMessageGetter(exception)
        } else {
            clientErrorText
        }
        clientError(message)
        throw StopHandlingException()
    } catch (exception: Exception) {
        val message = if (includeExceptionMessage) {
            serverErrorText + exceptionMessageGetter(exception)
        } else {
            serverErrorText
        }
        serverError(message)
        throw StopHandlingException()
    }
}

class StopHandlingException : RuntimeException()
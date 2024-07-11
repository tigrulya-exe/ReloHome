package exe.tigrulya.relohome.notifier.telegram.kt

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandler
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.HandleMessage
import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.extensions.filters.Filter


fun startsWith(text: String): Filter = Filter.Text and Filter.Custom { this.text?.startsWith(text) ?: false }

fun startsWith(prefixes: List<String>): Filter = Filter.Text and
        Filter.Custom {
            text?.let {
                prefixes.any { prefix ->
                    prefix.startsWith(it)
                }
            } ?: false
        }

fun Dispatcher.messageStartingWithPrefix(prefix: String, handleMessage: HandleMessage) =
    message(startsWith(prefix), handleMessage)

fun Dispatcher.messageStartingWithPrefix(vararg prefixes: String, handleMessage: HandleMessage) =
    message(startsWith(prefixes.toList()), handleMessage)

fun Dispatcher.messageWithWebApp(handleMessage: HandleMessage) =
    message(Filter.Custom { webAppData?.data != null }, handleMessage)

fun Dispatcher.command(command: String, handleCommand: HandleMessage) {
    addHandler(CommandHandler(command) {
        handleCommand(asMessageEnv())
    })
}

fun CommandHandlerEnvironment.asMessageEnv() = MessageHandlerEnvironment(
    bot, update, message
)
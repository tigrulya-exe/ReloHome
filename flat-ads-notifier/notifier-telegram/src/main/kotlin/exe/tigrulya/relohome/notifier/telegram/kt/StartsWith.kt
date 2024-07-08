package exe.tigrulya.relohome.notifier.telegram.kt

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.handlers.HandleMessage
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.extensions.filters.Filter


fun startsWith(text: String): Filter = Filter.Text and Filter.Custom { this.text?.startsWith(text) ?: false }

fun Dispatcher.messageStartingWith(text: String, handleMessage: HandleMessage) =
    message(startsWith(text), handleMessage)

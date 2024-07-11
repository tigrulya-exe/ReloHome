package exe.tigrulya.relohome.notifier.telegram.kt

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import com.github.kotlintelegrambot.entities.keyboard.WebAppInfo

class MainKeyboardProvider(private val handlerWebUrl: String) {
    companion object {
        const val ENABLED_BOT_BUTTON_TEXT = "🟢 Disable bot"
        const val DISABLED_BOT_BUTTON_TEXT = "🔴 Enable bot"
        const val OPTIONS_BUTTON_TEXT = "⚙️ Change settings"
        const val SUBSCRIPTION_INFO_BUTTON_TEXT = "💳 Subscription info"
        const val STATISTICS_BUTTON_TEXT = "📈 Statistics"
    }

    fun get(userId: String, isEnabled: Boolean = true): KeyboardReplyMarkup {
        val enableButton = KeyboardButton(
            text = if (isEnabled) ENABLED_BOT_BUTTON_TEXT else DISABLED_BOT_BUTTON_TEXT
        )

        val webappButton = KeyboardButton(
            text = OPTIONS_BUTTON_TEXT,
            webApp = WebAppInfo("${handlerWebUrl}/forms/tg_form/$userId")
        )

        val subscriptionInfoButton = KeyboardButton(
            text = SUBSCRIPTION_INFO_BUTTON_TEXT
        )

        val statisticsButton = KeyboardButton(
            text = STATISTICS_BUTTON_TEXT
        )

        return KeyboardReplyMarkup(
            keyboard = listOf(
                listOf(enableButton, webappButton),
                listOf(subscriptionInfoButton, statisticsButton),
            ),
            resizeKeyboard = true
        )
    }
}
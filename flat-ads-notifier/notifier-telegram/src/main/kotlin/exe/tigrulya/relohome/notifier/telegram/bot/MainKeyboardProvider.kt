package exe.tigrulya.relohome.notifier.telegram.bot

import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.extensions.utils.types.buttons.webAppButton
import dev.inmo.tgbotapi.types.buttons.KeyboardMarkup
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardMarkup
import dev.inmo.tgbotapi.types.webapps.WebAppInfo
import dev.inmo.tgbotapi.utils.matrix
import dev.inmo.tgbotapi.utils.row


class MainKeyboardProvider(private val handlerWebUrl: String) {
    companion object {
        const val DISABLED_BOT_BUTTON_TEXT = "🟢 Enable bot"
        const val ENABLED_BOT_BUTTON_TEXT = "🔴 Disable bot"
        const val OPTIONS_BUTTON_TEXT = "⚙️ Change settings"
        const val SUBSCRIPTION_INFO_BUTTON_TEXT = "💳 Subscription info"
        const val STATISTICS_BUTTON_TEXT = "📈 Statistics"
    }

    fun get(userId: String, searchEnabled: Boolean = true): KeyboardMarkup {
        return ReplyKeyboardMarkup(
            matrix {
                row {
                    simpleButton(
                        if (searchEnabled) ENABLED_BOT_BUTTON_TEXT else DISABLED_BOT_BUTTON_TEXT
                    )
                    webAppButton(
                        text = OPTIONS_BUTTON_TEXT,
                        webApp = WebAppInfo("${handlerWebUrl}/forms/tg_form/$userId")
                    )
                }
                row {
                    simpleButton(SUBSCRIPTION_INFO_BUTTON_TEXT)
                    simpleButton(STATISTICS_BUTTON_TEXT)
                }
            },
            resizeKeyboard = true
        )
    }
}
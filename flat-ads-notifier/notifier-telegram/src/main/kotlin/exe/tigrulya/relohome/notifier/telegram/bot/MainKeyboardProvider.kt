package exe.tigrulya.relohome.notifier.telegram.bot

import dev.inmo.tgbotapi.extensions.utils.types.buttons.webAppButton
import dev.inmo.tgbotapi.types.buttons.KeyboardMarkup
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardMarkup
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.webapps.WebAppInfo
import dev.inmo.tgbotapi.utils.matrix
import dev.inmo.tgbotapi.utils.row


class MainKeyboardProvider(private val handlerWebUrl: String) {
    companion object {
        const val DISABLED_BOT_BUTTON_TEXT = "🟢 Enable bot"
        const val ENABLED_BOT_BUTTON_TEXT = "🔴 Disable bot"
        const val OPTIONS_BUTTON_TEXT = "⚙️ Change search options"
        const val SUBSCRIPTION_INFO_BUTTON_TEXT = "💳 Subscription info"
        const val STATISTICS_BUTTON_TEXT = "📈 Statistics"
        const val CHANGE_LOCALE_BUTTON_TEXT = "\uD83C\uDDEC\uD83C\uDDE7 Change language"
    }

    // reuse buttons to reduce object creation
    private val subscriptionInfoButton = SimpleKeyboardButton(SUBSCRIPTION_INFO_BUTTON_TEXT)
    private val statisticsButton = SimpleKeyboardButton(STATISTICS_BUTTON_TEXT)
    private val enabledSearchButton = SimpleKeyboardButton(ENABLED_BOT_BUTTON_TEXT)
    private val disabledSearchButton = SimpleKeyboardButton(DISABLED_BOT_BUTTON_TEXT)
    private val changeLocaleButton = SimpleKeyboardButton(CHANGE_LOCALE_BUTTON_TEXT)

    fun get(userId: String, searchEnabled: Boolean = true): KeyboardMarkup {
        return ReplyKeyboardMarkup(
            matrix {
                row {
                    add(if (searchEnabled) enabledSearchButton else disabledSearchButton)
                    webAppButton(
                        text = OPTIONS_BUTTON_TEXT,
                        webApp = WebAppInfo("${handlerWebUrl}/forms/tg_form/$userId")
                    )
                }
                row {
                    add(subscriptionInfoButton)
                    add(statisticsButton)
                }
                row {
                    add(changeLocaleButton)
                }
            },
            resizeKeyboard = true
        )
    }
}
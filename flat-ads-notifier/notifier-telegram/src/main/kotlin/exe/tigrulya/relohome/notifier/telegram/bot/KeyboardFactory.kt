package exe.tigrulya.relohome.notifier.telegram.bot

import dev.inmo.tgbotapi.extensions.utils.types.buttons.ReplyKeyboardRowBuilder
import dev.inmo.tgbotapi.extensions.utils.types.buttons.dataButton
import dev.inmo.tgbotapi.extensions.utils.types.buttons.flatReplyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.buttons.KeyboardMarkup
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardMarkup
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.buttons.WebAppKeyboardButton
import dev.inmo.tgbotapi.types.webapps.WebAppInfo
import dev.inmo.tgbotapi.utils.matrix
import dev.inmo.tgbotapi.utils.row
import exe.tigrulya.relohome.localization.Localization
import exe.tigrulya.relohome.notifier.telegram.bot.handlers.LOCALE_VALUE_CALLBACK_DATA_PREFIX


class KeyboardFactory(
    val localization: Localization,
    private val handlerWebUrl: String
) {
    companion object {
        const val DISABLED_BOT_BUTTON_PREFIX = "🟢 "
        const val ENABLED_BOT_BUTTON_PREFIX = "🔴 "
        const val SUBSCRIPTION_INFO_BUTTON_PREFIX = "💳 "
        const val STATISTICS_BUTTON_PREFIX = "📈 "
        const val CHANGE_LOCALE_BUTTON_PREFIX = "\uD83C\uDDEC\uD83C\uDDE7 "
    }

    private data class Buttons(val localization: Localization, val locale: String) {
        val subscriptionInfoButton = button("keyboards.main.subscription-info")
        val statisticsButton = button("keyboards.main.statistics")
        val enabledSearchButton = button("keyboards.main.disable-search")
        val disabledSearchButton = button("keyboards.main.enable-search")
        val changeLocaleButton = button("keyboards.main.change-locale")

        private fun button(constantKey: String) = SimpleKeyboardButton(localization[locale, constantKey]!!.rawValue)
    }

    // reuse buttons to reduce object creation

    private val buttons: Map<String, Buttons> = mapOf(
        "en" to Buttons(localization, "en"),
        "ru" to Buttons(localization, "ru")
    )

    fun mainReplyKeyboard(userId: String, locale: String, searchEnabled: Boolean = true): KeyboardMarkup {
        val buttons = buttons[locale] ?: throw IllegalArgumentException("Wrong locale: $locale")

        return ReplyKeyboardMarkup(
            matrix {
                row {
                    add(if (searchEnabled) buttons.enabledSearchButton else buttons.disabledSearchButton)
                    webAppButton(userId, locale)
                }
                row {
                    add(buttons.subscriptionInfoButton)
                    add(buttons.statisticsButton)
                }
                row {
                    add(buttons.changeLocaleButton)
                }
            },
            resizeKeyboard = true,
            persistent = true
        )
    }

    fun searchOptionsReplyKeyboard(userId: String, locale: String): KeyboardMarkup =
        flatReplyKeyboard(resizeKeyboard = true) {
            webAppButton(userId, locale)
        }

    fun setLocaleInlineKeyboard(): KeyboardMarkup = inlineKeyboard {
        row {
            dataButton("\uD83C\uDDF7\uD83C\uDDFA Русский", LOCALE_VALUE_CALLBACK_DATA_PREFIX + "ru")
        }
        row {
            dataButton("\uD83C\uDDEC\uD83C\uDDE7 English", LOCALE_VALUE_CALLBACK_DATA_PREFIX + "en")
        }
    }

    private fun ReplyKeyboardRowBuilder.webAppButton(userId: String, locale: String) = add(
        WebAppKeyboardButton(
            text = localization[locale, "keyboards.main.search-options"]!!.rawValue,
            webApp = WebAppInfo("${handlerWebUrl}/forms/tg_form/$userId?locale=$locale")
        )
    )
}
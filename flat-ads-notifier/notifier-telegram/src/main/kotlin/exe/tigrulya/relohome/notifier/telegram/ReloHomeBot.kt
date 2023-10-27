package exe.tigrulya.relohome.notifier.telegram

import org.telegram.abilitybots.api.bot.AbilityBot
import org.telegram.abilitybots.api.objects.Ability
import org.telegram.abilitybots.api.objects.Locality
import org.telegram.abilitybots.api.objects.MessageContext
import org.telegram.abilitybots.api.objects.Privacy
import org.telegram.abilitybots.api.toggle.AbilityToggle
import org.telegram.abilitybots.api.toggle.CustomToggle
import org.telegram.telegrambots.bots.DefaultBotOptions
import java.util.concurrent.ThreadLocalRandom

class EnhancedWeatherNsuBot(botToken: String, botUsername: String, private val creatorId: Long) :
    AbilityBot(botToken, botUsername, createToggle(), createBotOptions()) {
    private val weatherNsuClient: WeatherNsuClient

    init {
        weatherNsuClient = HttpWeatherNsuClient()
    }

    override fun creatorId(): Long {
        return creatorId
    }

    fun currentTemperatureAbility(): Ability {
        return Ability
            .builder()
            .name("temperature_now")
            .locality(Locality.ALL)
            .privacy(Privacy.PUBLIC)
            .action { context: MessageContext ->
                currentTemperatureAction(
                    context
                )
            }
            .enableStats()
            .build()
    }

    fun defaultAbility(): Ability {
        return Ability
            .builder()
            .name(DEFAULT)
            .locality(Locality.ALL)
            .privacy(Privacy.PUBLIC)
            .action { context: MessageContext -> defaultAction(context) }
            .enableStats()
            .build()
    }

    private fun currentTemperatureAction(context: MessageContext) {
        val currentTemperature: Double = weatherNsuClient.getCurrentTemperature()
        silent.send(
            "Current temperature in akademgorodok is: %.1f \u00B0C".formatted(currentTemperature),
            context.chatId()
        )
    }

    private fun defaultAction(context: MessageContext) {
        val update = context.update()
        val message = update.message
        val source = message.from
        silent.send("Test, new beeeaach-" + ThreadLocalRandom.current().nextInt(), source.id)
    }

    companion object {
        private fun createToggle(): AbilityToggle {
            val customToggle = CustomToggle()
            customToggle.turnOff("demote")
            return customToggle
        }

        private fun createBotOptions(): DefaultBotOptions {
            val options = DefaultBotOptions()
            options.getUpdatesTimeout = 120
            return options
        }
    }
}

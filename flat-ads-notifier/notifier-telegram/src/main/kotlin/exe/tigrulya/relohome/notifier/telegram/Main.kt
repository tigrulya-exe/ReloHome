package exe.tigrulya.relohome.notifier.telegram

import exe.tigrulya.relohome.api.grpc.GrpcUserHandlerClient
import exe.tigrulya.relohome.kafka.KafkaConsumerConfig
import exe.tigrulya.relohome.notifier.KafkaFlatAdConsumer
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import kotlin.concurrent.thread
import kotlin.time.Duration.Companion.seconds

private const val MY_ID: Long = 479226955
private const val BOT_USERNAME = "relo_home_bot"

fun main(args: Array<String>) = NotifierEntryPoint.start()

object NotifierEntryPoint {
    fun start() {
        val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
        val reloHomeBot = ReloHomeBot(
            botToken = System.getenv("BOT_TOKEN"),
            botUsername = BOT_USERNAME,
            creatorId = MY_ID,
            GrpcUserHandlerClient("localhost:8999"),
            JsonSearchOptionsDeserializer()
        )
        botsApi.registerBot(reloHomeBot)

        val kafkaConsumerConfig = KafkaConsumerConfig(
            topics = listOf("flat_notifier_ads"),
            group = "flat_notifier",
            fetchTimeout = 1.seconds,
            bootstrapServers = "localhost:9094"
        )

        val flatAdConsumer = KafkaFlatAdConsumer(kafkaConsumerConfig)

        thread {
            flatAdConsumer.handleAds { userIds, flatAd ->
                reloHomeBot.onNewAd(userIds, flatAd)
            }
        }
    }
}
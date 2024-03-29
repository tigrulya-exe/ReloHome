package exe.tigrulya.relohome.notifier.telegram

import exe.tigrulya.relohome.api.blocking
import exe.tigrulya.relohome.api.grpc.GrpcUserHandlerClient
import exe.tigrulya.relohome.config.Configuration
import exe.tigrulya.relohome.kafka.KafkaConsumerConfig
import exe.tigrulya.relohome.kafka.splitTopics
import exe.tigrulya.relohome.notifier.KafkaFlatAdConsumer
import exe.tigrulya.relohome.notifier.options.KafkaConfigOptions.KAFKA_FLAT_AD_CONSUMER_BOOTSTRAP_SERVERS
import exe.tigrulya.relohome.notifier.options.KafkaConfigOptions.KAFKA_FLAT_AD_CONSUMER_GROUP
import exe.tigrulya.relohome.notifier.options.KafkaConfigOptions.KAFKA_FLAT_AD_CONSUMER_TOPICS
import exe.tigrulya.relohome.notifier.options.KafkaConfigOptions.KAFKA_FLAT_AD_FETCH_TIMEOUT
import exe.tigrulya.relohome.notifier.telegram.bot.ReloHomeBot
import exe.tigrulya.relohome.notifier.telegram.config.ConfigOptions.FLAT_AD_HANDLER_GRPC_GATEWAY_HOSTNAME
import exe.tigrulya.relohome.notifier.telegram.config.ConfigOptions.FLAT_AD_HANDLER_HTTP_GATEWAY_HOSTNAME
import exe.tigrulya.relohome.notifier.telegram.config.ConfigOptions.FLAT_AD_NOTIFIER_TG_BOT_NAME
import exe.tigrulya.relohome.notifier.telegram.config.ConfigOptions.FLAT_AD_NOTIFIER_TG_CREATOR_ID
import exe.tigrulya.relohome.notifier.telegram.config.ConfigOptions.FLAT_AD_NOTIFIER_TG_REQUESTS_PER_SEC
import exe.tigrulya.relohome.notifier.telegram.config.ConfigOptions.FLAT_AD_NOTIFIER_TG_TOKEN
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import kotlin.concurrent.thread

fun main() = TgNotifierEntryPoint.start()

object TgNotifierEntryPoint {
    fun start() {
        val config: Configuration = Configuration.fromResource("notifier-tg.yaml")

        val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
        val reloHomeBot = ReloHomeBot(
            botToken = config.getOptional(FLAT_AD_NOTIFIER_TG_TOKEN) ?: System.getenv("BOT_TOKEN"),
            botUsername = config.get(FLAT_AD_NOTIFIER_TG_BOT_NAME),
            creatorId = config.get(FLAT_AD_NOTIFIER_TG_CREATOR_ID),
            userHandlerGateway = GrpcUserHandlerClient(config.get(FLAT_AD_HANDLER_GRPC_GATEWAY_HOSTNAME)).blocking(),
            handlerWebUrl = config.get(FLAT_AD_HANDLER_HTTP_GATEWAY_HOSTNAME),
            requestsPerSecond = config.get(FLAT_AD_NOTIFIER_TG_REQUESTS_PER_SEC)
        )
        botsApi.registerBot(reloHomeBot)

        val kafkaConsumerConfig = KafkaConsumerConfig(
            bootstrapServers = config.get(KAFKA_FLAT_AD_CONSUMER_BOOTSTRAP_SERVERS),
            topics = splitTopics(config.get(KAFKA_FLAT_AD_CONSUMER_TOPICS)),
            group = config.get(KAFKA_FLAT_AD_CONSUMER_GROUP),
            fetchTimeout = config.get(KAFKA_FLAT_AD_FETCH_TIMEOUT)
        )

        val flatAdConsumer = KafkaFlatAdConsumer(kafkaConsumerConfig)

        thread {
            flatAdConsumer.handleAds { userIds, flatAd ->
                reloHomeBot.onNewAd(userIds, flatAd)
            }
        }
    }
}
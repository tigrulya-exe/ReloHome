package exe.tigrulya.relohome.notifier.telegram

import exe.tigrulya.relohome.api.FlatAdNotifierGateway
import exe.tigrulya.relohome.api.grpc.GrpcUserHandlerClient
import exe.tigrulya.relohome.api.user_handler.UserHandlerGateway
import exe.tigrulya.relohome.config.Configuration
import exe.tigrulya.relohome.kafka.KafkaConsumerConfig
import exe.tigrulya.relohome.kafka.splitTopics
import exe.tigrulya.relohome.notifier.KafkaFlatAdConsumer
import exe.tigrulya.relohome.notifier.options.KafkaConfigOptions.KAFKA_FLAT_AD_CONSUMER_BOOTSTRAP_SERVERS
import exe.tigrulya.relohome.notifier.options.KafkaConfigOptions.KAFKA_FLAT_AD_CONSUMER_GROUP
import exe.tigrulya.relohome.notifier.options.KafkaConfigOptions.KAFKA_FLAT_AD_CONSUMER_TOPICS
import exe.tigrulya.relohome.notifier.options.KafkaConfigOptions.KAFKA_FLAT_AD_FETCH_TIMEOUT
import exe.tigrulya.relohome.notifier.telegram.config.ConfigOptions.FLAT_AD_HANDLER_GRPC_GATEWAY_HOSTNAME
import exe.tigrulya.relohome.notifier.telegram.config.ConfigOptions.FLAT_AD_HANDLER_HTTP_GATEWAY_HOSTNAME
import exe.tigrulya.relohome.notifier.telegram.config.ConfigOptions.FLAT_AD_NOTIFIER_TG_REQUESTS_PER_SEC
import exe.tigrulya.relohome.notifier.telegram.config.ConfigOptions.FLAT_AD_NOTIFIER_TG_TOKEN
import exe.tigrulya.relohome.notifier.telegram.bot.ReloHomeTelegramBot
import exe.tigrulya.relohome.notifier.telegram.bot.TelegramFlatAdNotifier
import kotlin.concurrent.thread

suspend fun main() = TgNotifierEntryPoint.startRemote()

object TgNotifierEntryPoint {
    suspend fun startRemote() {
        val config: Configuration = Configuration.fromResource("notifier-tg.yaml")

        val kafkaConsumerConfig = KafkaConsumerConfig(
            bootstrapServers = config.get(KAFKA_FLAT_AD_CONSUMER_BOOTSTRAP_SERVERS),
            topics = splitTopics(config.get(KAFKA_FLAT_AD_CONSUMER_TOPICS)),
            group = config.get(KAFKA_FLAT_AD_CONSUMER_GROUP),
            fetchTimeout = config.get(KAFKA_FLAT_AD_FETCH_TIMEOUT)
        )

        // TODO add exception mapping to GrpcUserHandlerClient
        val userHandlerGateway = GrpcUserHandlerClient(config.get(FLAT_AD_HANDLER_GRPC_GATEWAY_HOSTNAME))

        val notifier = startInternal(config, userHandlerGateway)

        val flatAdConsumer = KafkaFlatAdConsumer(kafkaConsumerConfig)
        thread {
            flatAdConsumer.handleAds { userIds, flatAd ->
                notifier.onNewAd(userIds, flatAd)
            }
        }
    }

    suspend fun startInPlace(userHandlerGateway: UserHandlerGateway): FlatAdNotifierGateway {
        val config: Configuration = Configuration.fromResource("notifier-tg.yaml")
        return startInternal(config, userHandlerGateway)
    }

    private suspend fun startInternal(
        config: Configuration,
        userHandlerGateway: UserHandlerGateway,
    ): FlatAdNotifierGateway {
        val reloHomeBot = ReloHomeTelegramBot(
            botToken = config.getOptional(FLAT_AD_NOTIFIER_TG_TOKEN) ?: System.getenv("BOT_TOKEN"),
            userHandlerGateway = userHandlerGateway,
            handlerWebUrl = config.get(FLAT_AD_HANDLER_HTTP_GATEWAY_HOSTNAME),
            requestsPerSecond = config.get(FLAT_AD_NOTIFIER_TG_REQUESTS_PER_SEC)
        )

        reloHomeBot.start()

        return TelegramFlatAdNotifier(reloHomeBot.tgBot)
    }
}
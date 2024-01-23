package exe.tigrulya.relohome.handler.config

import exe.tigrulya.relohome.config.ConfigOption
import kotlin.time.Duration.Companion.seconds

object ConfigOptions {
    val KAFKA_FLAT_AD_CONSUMER_BOOTSTRAP_SERVERS = ConfigOption(
        name = "kafka.flat-ads.consumer.bootstrap-servers",
        defaultValue = "localhost:9094"
    )

    val KAFKA_FLAT_AD_CONSUMER_TOPICS = ConfigOption(
        name = "kafka.flat-ads.consumer.topic",
        defaultValue = "flat_handler_ads"
    )

    val KAFKA_FLAT_AD_CONSUMER_GROUP = ConfigOption(
        name = "kafka.flat-ads.consumer.group",
        defaultValue = "flat_handler"
    )

    val KAFKA_FLAT_AD_FETCH_TIMEOUT = ConfigOption(
        name = "kafka.flat-ads.consumer.fetch.timeout",
        defaultValue = 1.seconds
    )

    val KAFKA_FLAT_AD_PRODUCER_TOPIC = ConfigOption(
        name = "kafka.flat-ads.producer.topic",
        defaultValue = "flat_notifier_ads"
    )

    val KAFKA_FLAT_AD_PRODUCER_BOOTSTRAP_SERVERS = ConfigOption(
        name = "kafka.flat-ads.producer.bootstrap-servers",
        defaultValue = "localhost:9094"
    )

    val FLAT_AD_HANDLER_GATEWAY_PORT = ConfigOption(
        name = "flat-ads.handler.gateway.port",
        defaultValue = 8999
    )

    val FLAT_AD_HANDLER_DB_URL = ConfigOption(
        name = "flat-ads.handler.db.url",
        defaultValue = "jdbc:postgresql://localhost:65432/ReloHome?user=root&password=toor"
    )
}
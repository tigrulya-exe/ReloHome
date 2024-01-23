package exe.tigrulya.relohome.notifier.options

import exe.tigrulya.relohome.config.ConfigOption
import kotlin.time.Duration.Companion.seconds

object KafkaConfigOptions {
    val KAFKA_FLAT_AD_CONSUMER_BOOTSTRAP_SERVERS = ConfigOption(
        name = "kafka.flat-ads.consumer.bootstrap-servers",
        defaultValue = "localhost:9094"
    )

    val KAFKA_FLAT_AD_CONSUMER_TOPICS = ConfigOption(
        name = "kafka.flat-ads.consumer.topic",
        defaultValue = "flat_notifier_ads"
    )

    val KAFKA_FLAT_AD_CONSUMER_GROUP = ConfigOption(
        name = "kafka.flat-ads.consumer.group",
        defaultValue = "flat_notifier"
    )

    val KAFKA_FLAT_AD_FETCH_TIMEOUT = ConfigOption(
        name = "kafka.flat-ads.consumer.fetch.timeout",
        defaultValue = 1.seconds
    )
}
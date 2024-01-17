package exe.tigrulya.relohome.kafka

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

open class KafkaConfig(
    val bootstrapServers: String,
    val additionalConfig: MutableMap<String, Any> = mutableMapOf()
)

class KafkaProducerConfig(
    val topic: String,
    bootstrapServers: String,
    additionalConfig: MutableMap<String, Any> = mutableMapOf()
) : KafkaConfig(bootstrapServers, additionalConfig)

class KafkaConsumerConfig(
    val topics: List<String>,
    val group: String,
    val fetchTimeout: Duration = 500.milliseconds,
    bootstrapServers: String,
    additionalConfig: MutableMap<String, Any> = mutableMapOf()
) : KafkaConfig(bootstrapServers, additionalConfig)

fun splitTopics(rawTopics: String): List<String> {
    return rawTopics.split(",")
}
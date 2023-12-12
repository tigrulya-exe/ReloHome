package exe.tigrulya.relohome.kafka

import exe.tigrulya.relohome.util.LoggerProperty
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.Deserializer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

class BaseKafkaConsumer<K, V>(
    val topics: List<String>,
    val group: String,
    val bootstrapServers: String,
    val keyDeserializer: Class<Deserializer<K>>,
    val valueDeserializer: Class<Deserializer<V>>,
    val fetchTimeout: Duration = 500.milliseconds,
    val additionalConfig: Map<String, String> = mapOf()
) {

    private val logger by LoggerProperty()

    fun consumeWithKey(recordHandler: (K, V) -> Unit) = KafkaConsumer<K, V>(buildConfig()).use {
        it.subscribe(topics)
        val timeout = fetchTimeout.toJavaDuration()
        repeatUntilSome {
            it.poll(timeout)
                .map { record ->
                    logger.debug("Handle incoming record: {}", record)
                    recordHandler.invoke(record.key(), record.value())
                }
        }
    }

    fun consume(recordHandler: (V) -> Unit) = consumeWithKey { _, value -> recordHandler.invoke(value) }

    private fun buildConfig(): Map<String, String> = additionalConfig.toMutableMap().also {
        it[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        it[ConsumerConfig.GROUP_ID_CONFIG] = group
        it[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = keyDeserializer.name
        it[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = valueDeserializer.name
    }

    private tailrec fun <T> repeatUntilSome(block: () -> T?): T = block() ?: repeatUntilSome(block)
}
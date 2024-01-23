package exe.tigrulya.relohome.kafka

import exe.tigrulya.relohome.util.LoggerProperty
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.Deserializer
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.toJavaDuration

open class BaseKafkaConsumer<K, V>(
    val kafkaConfig: KafkaConsumerConfig,
    val keyDeserializer: Class<out Deserializer<K>>,
    val valueDeserializer: Class<out Deserializer<*>>
) {

    private val logger by LoggerProperty()
    private val isRunning = AtomicBoolean(true)

    suspend fun consumeWithKey(recordHandler: suspend (K, V) -> Unit) = KafkaConsumer<K, V>(buildConfig()).use {
        it.subscribe(kafkaConfig.topics)
        val timeout = kafkaConfig.fetchTimeout.toJavaDuration()

        while (isRunning.get()) {
            it.poll(timeout)
                .forEach { record ->
                    logger.debug("Handle incoming record: {}", record)
                    recordHandler.invoke(record.key(), record.value())
                }
        }
    }

    suspend fun consume(recordHandler: suspend (V) -> Unit) = consumeWithKey { _, value -> recordHandler.invoke(value) }

    fun stop() {
        isRunning.set(false)
    }

    private fun buildConfig(): Map<String, Any> = kafkaConfig.additionalConfig.toMutableMap().also {
        it[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaConfig.bootstrapServers
        it[ConsumerConfig.GROUP_ID_CONFIG] = kafkaConfig.group
        it[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = keyDeserializer.name
        it[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = valueDeserializer.name
    }
}
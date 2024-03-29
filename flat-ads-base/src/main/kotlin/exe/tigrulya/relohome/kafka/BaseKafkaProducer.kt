package exe.tigrulya.relohome.kafka

import exe.tigrulya.relohome.util.LoggerProperty
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.Serializer
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class BaseKafkaProducer<K, V>(
    private val kafkaConfig: KafkaProducerConfig,
    private val keySerializer: Class<out Serializer<*>>,
    private val valueSerializer: Class<out Serializer<*>>
) {

    private val logger by LoggerProperty()

    private val producer: KafkaProducer<K, V> = KafkaProducer<K, V>(buildConfig())

    suspend fun send(key: K, value: V) = suspendCoroutine { continuation ->
        val record = ProducerRecord(kafkaConfig.topic, key, value)
        producer.send(record) { metadata, exception ->
            exception?.let {
                logger.error("Error sending record to kafka", exception)
                continuation.resumeWithException(exception)
            } ?: continuation.resume(metadata)
        }
    }

    private fun buildConfig(): Map<String, Any> = kafkaConfig.additionalConfig.toMutableMap().also {
        it[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaConfig.bootstrapServers
        it[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = keySerializer.name
        it[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = valueSerializer.name
    }
}
package exe.tigrulya.relohome.notifier

import exe.tigrulya.relohome.api.FlatAdOuterClass
import exe.tigrulya.relohome.kafka.BaseKafkaConsumer
import exe.tigrulya.relohome.kafka.KafkaConsumerConfig
import exe.tigrulya.relohome.kafka.serde.ProtobufDeserializer
import exe.tigrulya.relohome.model.FlatAd
import exe.tigrulya.relohome.proto.toDomain
import kotlinx.coroutines.runBlocking
import org.apache.kafka.common.serialization.StringDeserializer

class KafkaFlatAdConsumer(kafkaConfig: KafkaConsumerConfig) {

    private val kafkaConsumer = BaseKafkaConsumer<String, FlatAdOuterClass.FlatAdMessage>(
        kafkaConfig = kafkaConfig.also {
            it.additionalConfig[ProtobufDeserializer.MESSAGE_CLASS] = FlatAdOuterClass.FlatAdMessage::class.java
        },
        keyDeserializer = StringDeserializer::class.java,
        valueDeserializer = ProtobufDeserializer::class.java
    )

    fun handleAds(handler: suspend (List<String>, FlatAd) -> Unit) = runBlocking {
        kafkaConsumer.consume { flatAdProto ->
            val flatAdMessage = flatAdProto.toDomain()
            handler.invoke(flatAdMessage.userIds, flatAdMessage.flatAd)
        }
    }
}
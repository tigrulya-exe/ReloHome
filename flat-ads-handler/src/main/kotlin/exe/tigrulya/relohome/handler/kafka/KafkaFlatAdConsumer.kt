package exe.tigrulya.relohome.handler.kafka

import exe.tigrulya.relohome.api.FlatAdOuterClass
import exe.tigrulya.relohome.kafka.BaseKafkaConsumer
import exe.tigrulya.relohome.kafka.KafkaConsumerConfig
import exe.tigrulya.relohome.kafka.serde.ProtobufDeserializer
import exe.tigrulya.relohome.model.FlatAd
import exe.tigrulya.relohome.proto.toDomain
import kotlinx.coroutines.runBlocking
import org.apache.kafka.common.serialization.StringDeserializer

class KafkaFlatAdConsumer(kafkaConfig: KafkaConsumerConfig) {

    private val kafkaConsumer = BaseKafkaConsumer<String, FlatAdOuterClass.FlatAd>(
        kafkaConfig = kafkaConfig.also {
            it.additionalConfig[ProtobufDeserializer.MESSAGE_CLASS] = FlatAdOuterClass.FlatAd::class.java
        },
        keyDeserializer = StringDeserializer::class.java,
        valueDeserializer = ProtobufDeserializer::class.java,
    )

    fun handleAds(handler: suspend (FlatAd) -> Unit) = runBlocking {
        kafkaConsumer.consume { flatAdProto ->
            handler.invoke(flatAdProto.toDomain())
        }
    }
}
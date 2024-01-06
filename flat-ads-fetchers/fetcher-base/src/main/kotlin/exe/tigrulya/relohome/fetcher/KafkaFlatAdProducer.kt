package exe.tigrulya.relohome.fetcher

import exe.tigrulya.relohome.api.FlatAdHandlerGateway
import exe.tigrulya.relohome.api.FlatAdOuterClass
import exe.tigrulya.relohome.kafka.BaseKafkaProducer
import exe.tigrulya.relohome.kafka.KafkaProducerConfig
import exe.tigrulya.relohome.kafka.serde.ProtobufSerializer
import exe.tigrulya.relohome.model.FlatAd
import exe.tigrulya.relohome.proto.toProto
import org.apache.kafka.common.serialization.StringSerializer

class KafkaFlatAdProducer(kafkaConfig: KafkaProducerConfig) : FlatAdHandlerGateway {

    private val producer = BaseKafkaProducer<String, FlatAdOuterClass.FlatAd>(
        kafkaConfig = kafkaConfig,
        keySerializer = StringSerializer::class.java,
        valueSerializer = ProtobufSerializer::class.java
    )

    override suspend fun handle(flatAd: FlatAd) {
        // todo think about key
        producer.send(flatAd.address.city.name, flatAd.toProto())
    }
}
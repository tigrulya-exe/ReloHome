package exe.tigrulya.relohome.handler.kafka

import exe.tigrulya.relohome.api.FlatAdNotifierGateway
import exe.tigrulya.relohome.api.FlatAdOuterClass
import exe.tigrulya.relohome.kafka.BaseKafkaProducer
import exe.tigrulya.relohome.kafka.KafkaProducerConfig
import exe.tigrulya.relohome.kafka.serde.ProtobufSerializer
import exe.tigrulya.relohome.model.FlatAd
import exe.tigrulya.relohome.model.FlatAdMessage
import exe.tigrulya.relohome.model.UserInfo
import exe.tigrulya.relohome.proto.toProto
import org.apache.kafka.common.serialization.StringSerializer

class KafkaFlatAdProducer(kafkaConfig: KafkaProducerConfig) : FlatAdNotifierGateway {

    private val producer = BaseKafkaProducer<String, FlatAdOuterClass.FlatAdMessage>(
        kafkaConfig = kafkaConfig,
        keySerializer = StringSerializer::class.java,
        valueSerializer = ProtobufSerializer::class.java
    )

    override suspend fun onNewAd(users: List<UserInfo>, flatAd: FlatAd) {
        // todo think about key
        producer.send(flatAd.address.city.name, FlatAdMessage(users, flatAd).toProto())
    }
}
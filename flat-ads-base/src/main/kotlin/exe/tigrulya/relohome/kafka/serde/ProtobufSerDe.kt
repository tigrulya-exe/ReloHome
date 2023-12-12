package exe.tigrulya.relohome.kafka.serde

import com.google.protobuf.Message
import com.google.protobuf.Parser
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serializer

class ProtobufSerializer : Serializer<Message> {
    override fun serialize(topic: String?, data: Message): ByteArray = data.toByteArray()
}

class ProtobufDeserializer<T : Message> : Deserializer<T> {

    companion object {
        const val MESSAGE_CLASS: String = "protobuf.deserializer.class"
    }

    private lateinit var protoParser: Parser<T>

    @Suppress("UNCHECKED_CAST")
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
        val innerClassRaw = configs?.get(MESSAGE_CLASS)
            ?: throw IllegalArgumentException("Required argument $MESSAGE_CLASS not provided")

        val innerClass: Class<T> = when (innerClassRaw) {
            is Class<*> -> innerClassRaw as Class<T>
            else -> throw IllegalArgumentException("$MESSAGE_CLASS option value's type should be class")
        }

        protoParser = innerClass.getDeclaredConstructor().newInstance().parserForType as Parser<T>
    }

    override fun deserialize(topic: String?, data: ByteArray?): T = protoParser.parseFrom(data)

}
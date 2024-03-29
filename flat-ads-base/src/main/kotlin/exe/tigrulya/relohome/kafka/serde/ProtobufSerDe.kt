package exe.tigrulya.relohome.kafka.serde

import com.google.protobuf.Message
import com.google.protobuf.Parser
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serializer

class ProtobufSerializer<T : Message> : Serializer<T> {
    override fun serialize(topic: String?, data: T): ByteArray = data.toByteArray()
}

class ProtobufDeserializer<T : Message> : Deserializer<T> {

    companion object {
        const val MESSAGE_CLASS: String = "protobuf.message.class"
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

        protoParser = innerClass.getMethod("parser").invoke(null) as Parser<T>
    }

    override fun deserialize(topic: String?, data: ByteArray?): T = protoParser.parseFrom(data)

}
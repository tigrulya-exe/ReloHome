package exe.tigrulya.relohome.kafka

data class KafkaConfig(
    val topic: String,
    val bootstrapServers: String,
    val additionalConfig: Map<String, String> = mapOf()
)
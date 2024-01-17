package exe.tigrulya.relohome.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

class YamlConfigurationParser: ConfigurationParser {

    private val mapper = jacksonObjectMapper()

    override fun parse(rawConfig: String): Map<String, Any> {
        return mapper.readValue(rawConfig)
    }
}
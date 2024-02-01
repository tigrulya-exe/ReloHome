package exe.tigrulya.relohome.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

class YamlConfigurationParser : ConfigurationParser {

    private val mapper = ObjectMapper(YAMLFactory())
        .registerKotlinModule()

    override fun parse(rawConfig: String): Map<String, Any> {
        return flattenMap(mapper.readValue(rawConfig))
    }

    private fun flattenMap(initial: Map<String, Any>): Map<String, Any> {
        // TODO do it in func style
        return initial
    }
}
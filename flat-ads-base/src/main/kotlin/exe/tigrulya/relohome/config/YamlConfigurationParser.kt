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
        val result = mutableMapOf<String, Any>()
        flattenMapRecursive(mutableListOf(), initial, result)
        return result
    }

    private fun flattenMapRecursive(keys: MutableList<String>, node: Any, acc: MutableMap<String, Any>) {
        if (node is java.util.Map<*, *>) {
            node.forEach { key, value ->
                keys.add(key as String)
                flattenMapRecursive(keys, value!!, acc)
                keys.removeLast()
            }
            return
        }

        acc[keys.joinToString(".")] = node
    }
}
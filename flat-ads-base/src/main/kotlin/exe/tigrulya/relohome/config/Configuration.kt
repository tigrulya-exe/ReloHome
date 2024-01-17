package exe.tigrulya.relohome.config

import java.nio.file.Files
import java.nio.file.Path

data class ConfigOption<T>(
    val name: String,
    val defaultValue: T? = null,
    val required: Boolean = defaultValue == null,
    val description: String? = null
)

interface ConfigurationParser {
    fun parse(rawConfig: String): Map<String, Any>
}

class Configuration(configMap: Map<String, Any> = mapOf()) {

    private val configMap: MutableMap<String, Any> = configMap.toMutableMap()

    companion object {
        fun fromResource(
            resourcePath: String,
            parser: ConfigurationParser = YamlConfigurationParser()
        ): Configuration {
            val resource = Companion::class.java.classLoader.getResource(resourcePath)
                ?: return Configuration()
            val rawString = Files.readString(Path.of(resource.toURI()))
            return Configuration(parser.parse(rawString))
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getOptional(option: ConfigOption<T>): T? {
        return (configMap[option.name] as T)
            ?: option.defaultValue
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(option: ConfigOption<T>): T {
        return (configMap[option.name] as T)
            ?: option.defaultValue
            ?: throw IllegalArgumentException("Value not found for option: ${option.name}")
    }

    fun <T: Any> set(option: ConfigOption<T>, value: T) {
        configMap[option.name] = value
    }
}
package exe.tigrulya.relohome.config

import exe.tigrulya.relohome.util.LoggerProperty
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration

data class ConfigOption<T>(
    val name: String,
    val defaultValue: T? = null,
    val required: Boolean = defaultValue == null,
    val description: String? = null
)

interface ConfigurationParser {
    fun parse(rawConfig: String): Map<String, Any>
    fun parseFromResource(resourcePath: String): Map<String, Any> {
        val resource = ConfigurationParser::class.java.classLoader.getResource(resourcePath)
            ?: return emptyMap()
        val rawConfig = Files.readString(Path.of(resource.toURI()))
        return parse(rawConfig)
    }

    fun parseFromFile(file: String): Map<String, Any> {
        val rawConfig = Files.readString(Path.of(file))
        return parse(rawConfig)
    }
}

interface Configuration {
    companion object {
        private val logger by LoggerProperty()

        fun fromResource(
            resourcePath: String,
            parser: ConfigurationParser = YamlConfigurationParser()
        ): Configuration {
            val configs = parser.parseFromResource(resourcePath)
            if (configs.isEmpty()) {
                logger.warn("Configuration resource $resourcePath not found. Using default configuration.")
            }

            return MapConfiguration(configs)
        }
    }

    fun <T> getOptional(option: ConfigOption<T>): T?

    fun <T> get(option: ConfigOption<T>): T

    fun getDuration(option: ConfigOption<String>): Duration
}

interface MutableConfiguration: Configuration {
    fun <T : Any> set(option: ConfigOption<T>, value: T)
}

class MapConfiguration(configMap: Map<String, Any> = mapOf()): MutableConfiguration {

    private val configMap: MutableMap<String, Any> = configMap.toMutableMap()

    @Suppress("UNCHECKED_CAST")
    override fun <T> getOptional(option: ConfigOption<T>): T? {
        return (configMap[option.name] as T)
            ?: option.defaultValue
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(option: ConfigOption<T>): T {
        return (configMap[option.name] as T)
            ?: option.defaultValue
            ?: throw IllegalArgumentException("Value not found for option: ${option.name}")
    }

    override fun getDuration(option: ConfigOption<String>): Duration {
        return Duration.parse("PT${get(option)}")
    }

    override fun <T : Any> set(option: ConfigOption<T>, value: T) {
        configMap[option.name] = value
    }
}

// TODO somehow support fetching options from env
class CompositeConfiguration(private val configs: List<Configuration>): Configuration {
    init {
        if (configs.isEmpty()) {
            throw IllegalArgumentException("List of configs shouldn't be empty")
        }
    }

    override fun <T> getOptional(option: ConfigOption<T>): T? {
        return configs.firstNotNullOfOrNull { it.getOptional(option) }
    }

    override fun <T> get(option: ConfigOption<T>): T {
        return getOptional(option)
            ?: throw IllegalArgumentException("Value not found for option: ${option.name}")
    }

    override fun getDuration(option: ConfigOption<String>): Duration {
        return configs.first().getDuration(option)
    }
}

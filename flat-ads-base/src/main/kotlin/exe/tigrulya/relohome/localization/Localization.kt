package exe.tigrulya.relohome.localization

import exe.tigrulya.relohome.config.ConfigurationParser
import exe.tigrulya.relohome.config.YamlConfigurationParser
import exe.tigrulya.relohome.template.MustacheTemplateEngine
import exe.tigrulya.relohome.template.TemplateEngine
import java.io.File

typealias TemplateEngineProvider = () -> TemplateEngine

class Localization(
    localeDirPath: String,
    constantsParser: ConfigurationParser = YamlConfigurationParser(),
    templateEngineProvider: TemplateEngineProvider = { MustacheTemplateEngine() }) {

    private val locales: Map<String, Locale> = getLocalePaths(localeDirPath)
        .mapValues { Locale(templateEngineProvider.invoke(), constantsParser, it.value) }

    operator fun get(locale: String): Locale? = locales[locale]

    operator fun get(locale: String, constantId: String): LocaleConstant? = locales[locale]?.let { it[constantId] }

    private fun getLocalePaths(localeDirPath: String): Map<String, String> {
        val localeDirResource = Localization::class.java.classLoader.getResource(localeDirPath)
            ?: throw IllegalArgumentException("Locale dir not found: $localeDirPath")

        return File(localeDirResource.file).walk()
            .filter { it.isFile }
            .associateByTo(HashMap(), { it.nameWithoutExtension }) {
                it.toString()
            }
    }
}

class Locale(
    templateEngine: TemplateEngine = MustacheTemplateEngine(),
    constantsParser: ConfigurationParser = YamlConfigurationParser(),
    constantsPath: String) {

    private val constants: Map<String, LocaleConstant> = constantsParser.parseFileAsStrings(constantsPath)
        .mapValues { LocaleConstantImpl(it.key, it.value, templateEngine) }

    operator fun get(constantId: String): LocaleConstant? = constants[constantId]
}

interface LocaleConstant {
    val rawValue: String
    fun <T> eval(context: T): String
}

data class LocaleConstantImpl(
    private val id: String,
    override val rawValue: String,
    private val templateEngine: TemplateEngine): LocaleConstant {

    override fun <T> eval(context: T): String {
        return templateEngine.compile(id, rawValue, context as Any)
    }
}

fun ConfigurationParser.parseFileAsStrings(filePath: String): Map<String, String> {
    return parseFromFile(filePath).mapValues { it.value.toString() }
}
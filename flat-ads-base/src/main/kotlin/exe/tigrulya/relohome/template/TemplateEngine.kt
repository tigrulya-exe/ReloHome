package exe.tigrulya.relohome.template

interface TemplateEngine {
    fun <T> compile(templatePath: String, dataObject: T): String

    fun <T> compile(templateId: String, template: String, dataObject: T): String
}
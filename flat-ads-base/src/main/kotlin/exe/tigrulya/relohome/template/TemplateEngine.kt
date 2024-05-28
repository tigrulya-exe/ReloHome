package exe.tigrulya.relohome.template

interface TemplateEngine {
    fun compile(templatePath: String, vararg scopeObjects: Any): String

    fun compile(templateId: String, template: String, vararg scopeObjects: Any): String
}
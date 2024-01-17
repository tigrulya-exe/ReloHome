package exe.tigrulya.relohome.notifier.template

interface TemplateEngine {
    fun <T> compile(templatePath: String, dataObject: T): String
}
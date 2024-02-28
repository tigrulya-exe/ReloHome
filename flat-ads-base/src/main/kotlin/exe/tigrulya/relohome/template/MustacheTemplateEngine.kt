package exe.tigrulya.relohome.template

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import com.github.mustachejava.MustacheFactory
import java.io.StringReader
import java.io.StringWriter

open class MustacheTemplateEngine: TemplateEngine {
    private val factory: MustacheFactory = DefaultMustacheFactory()
    private val compiledTemplates = mutableMapOf<String, Mustache>()

    override fun <T> compile(templatePath: String, dataObject: T): String {
        val compiledTemplate = compiledTemplates.computeIfAbsent(templatePath) {
            factory.compile(templatePath)
        }
        return compileInternal(compiledTemplate, dataObject)
    }

    override fun <T> compile(templateId: String, template: String, dataObject: T): String {
        val compiledTemplate = compiledTemplates.computeIfAbsent(templateId) {
            factory.compile(StringReader(template), templateId)
        }

        return compileInternal(compiledTemplate, dataObject)
    }

    protected open fun <T> compileInternal(compiledTemplate: Mustache, dataObject: T): String {
        val writer = StringWriter()
        compiledTemplate.execute(writer, dataObject)
        return writer.toString()
    }
}

/** Not thread-safe! Use one instance of engine per thread. */
class ObjectReuseMustacheTemplateEngine: MustacheTemplateEngine() {
    private val writer = StringWriter()

    override fun <T> compileInternal(compiledTemplate: Mustache, dataObject: T): String {
        writer.buffer.setLength(0)
        compiledTemplate.execute(writer, dataObject)
        return writer.toString()
    }
}

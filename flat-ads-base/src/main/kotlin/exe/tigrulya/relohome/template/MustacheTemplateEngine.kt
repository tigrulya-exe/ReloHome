package exe.tigrulya.relohome.template

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import com.github.mustachejava.MustacheFactory
import java.io.StringReader
import java.io.StringWriter

open class MustacheTemplateEngine: TemplateEngine {
    private val factory: MustacheFactory = DefaultMustacheFactory()
    private val compiledTemplates = mutableMapOf<String, Mustache>()

    override fun compile(templatePath: String, vararg scopeObjects: Any): String {
        val compiledTemplate = compiledTemplates.computeIfAbsent(templatePath) {
            factory.compile(templatePath)
        }
        return compileInternal(compiledTemplate, *scopeObjects)
    }

    override fun compile(templateId: String, template: String, vararg scopeObjects: Any): String {
        val compiledTemplate = compiledTemplates.computeIfAbsent(templateId) {
            factory.compile(StringReader(template), templateId)
        }

        return compileInternal(compiledTemplate, *scopeObjects)
    }

    protected open fun compileInternal(compiledTemplate: Mustache, vararg scopeObjects: Any): String {
        val writer = StringWriter()
        compiledTemplate.execute(writer, scopeObjects)
        return writer.toString()
    }
}

/** Not thread-safe! Use one instance of engine per thread. */
class ObjectReuseMustacheTemplateEngine: MustacheTemplateEngine() {
    private val writer = StringWriter()

    override fun compileInternal(compiledTemplate: Mustache, vararg scopeObjects: Any): String {
        writer.buffer.setLength(0)
        compiledTemplate.execute(writer, scopeObjects)
        return writer.toString()
    }
}

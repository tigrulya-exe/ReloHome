package exe.tigrulya.relohome.notifier.template

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import com.github.mustachejava.MustacheFactory
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.Path


    /** Not thread-safe! Use one instance of engine per thread. */
class MustacheTemplateEngine: TemplateEngine {
    private val factory: MustacheFactory = DefaultMustacheFactory()
    private val compiledTemplates = mutableMapOf<String, Mustache>()
    private val writer = StringWriter()

    override fun <T> compile(templatePath: String, dataObject: T): String {
        val compiledTemplate = compiledTemplates.computeIfAbsent(templatePath) {
            factory.compile(templatePath)
        }

        writer.buffer.setLength(0)
        compiledTemplate.execute(writer, dataObject)
        return writer.toString()
    }
}
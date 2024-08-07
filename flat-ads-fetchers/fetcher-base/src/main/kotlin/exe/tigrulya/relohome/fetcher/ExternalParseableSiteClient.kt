package exe.tigrulya.relohome.fetcher

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.*
import io.ktor.util.reflect.*
import io.ktor.utils.io.*
import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class HtmlPageConverter<T>(
    private val parser: HtmlDomParser<T>,
    private val baseUrl: String
) : ContentConverter {
    override suspend fun serializeNullable(
        contentType: ContentType,
        charset: Charset,
        typeInfo: TypeInfo,
        value: Any?
    ): OutgoingContent {
        // we don't send any requests with payload
        return EmptyContent
    }

    override suspend fun deserialize(charset: Charset, typeInfo: TypeInfo, content: ByteReadChannel): T {
        return withContext(Dispatchers.IO) {
            val htmlPage: Document = Jsoup.parse(content.toInputStream(), charset.name(), baseUrl)
            val parsedResult = parser.parse(htmlPage)
            parsedResult
        }
    }
}

fun <T> Configuration.domConverter(parser: HtmlDomParser<T>, baseUrl: String) {
    val converter = HtmlPageConverter(parser, baseUrl)
    register(ContentType.Text.Html, converter)
}

abstract class ExternalSiteClient<T>(
    protected val baseUrl: String
) {
    protected val httpClient = HttpClient(CIO) {
        BrowserUserAgent()
        defaultRequest {
            url(baseUrl)
        }
//        install(Logging) {
//            level = LogLevel.NONE
//        }
        HttpResponseValidator {
            validateResponse { response ->
                when (response.status.value) {
                    401 ->
                        onAuthError(response.bodyAsText())
                    in 400..499 ->
                        throw RuntimeException("Http client error. Code: ${response.status}, message: ${response.bodyAsText()}")
                    in 500..599 ->
                        throw RuntimeException("Http server error. Code: ${response.status}, message: ${response.bodyAsText()}")
                }
            }
        }
        configureHttpClient(this)
    }

    protected open fun onAuthError(errorMessage: String) {
        throw RuntimeException("Auth error: $errorMessage")
    }

    protected open fun configureHttpClient(config: HttpClientConfig<CIOEngineConfig>) {
        // no-op
    }
}

abstract class ExternalParseableSiteClient<T>(baseUrl: String) : ExternalSiteClient<T>(baseUrl) {
    override fun configureHttpClient(config: HttpClientConfig<CIOEngineConfig>) {
        config.install(ContentNegotiation) {
            domConverter(htmlDomParser(), baseUrl)
        }
    }

    abstract fun htmlDomParser(): HtmlDomParser<T>
}
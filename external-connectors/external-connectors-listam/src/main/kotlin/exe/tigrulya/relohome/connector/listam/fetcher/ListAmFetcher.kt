package exe.tigrulya.relohome.connector.listam.fetcher

import exe.tigrulya.relohome.connector.listam.parser.DomParser
import exe.tigrulya.relohome.connector.listam.parser.ListAmDomParser
import exe.tigrulya.relohome.connector.listam.parser.ListAmFlatAd
import exe.tigrulya.relohome.connector.listam.parser.ListAmFlatAdInfo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
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

class HtmlPageConverter(
    private val parser: DomParser,
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

    override suspend fun deserialize(charset: Charset, typeInfo: TypeInfo, content: ByteReadChannel): Any {
        return withContext(Dispatchers.IO) {
            val htmlPage: Document = Jsoup.parse(content.toInputStream(), charset.name(), baseUrl)
            val parsedResult = parser.parse(htmlPage)
            parsedResult
        }
    }
}

fun Configuration.domConverter(parser: DomParser, baseUrl: String) {
    val converter = HtmlPageConverter(parser, baseUrl)
    register(ContentType.Text.Html, converter)
}

open class ExternalSiteClient<T>(
    protected val baseUrl: String,
    protected val domParser: DomParser
) {
    protected val httpClient = HttpClient(CIO) {
        BrowserUserAgent()
        defaultRequest {
            url(baseUrl)
        }
        install(ContentNegotiation) {
            domConverter(domParser, baseUrl)
        }
        install(Logging) {
            level = LogLevel.HEADERS
        }
    }
}

class ListAmClient(baseUrl: String) :
    ExternalSiteClient<ListAmFlatAd>(baseUrl, ListAmDomParser()) {
    suspend fun fetchAds(pageNum: Int = 1): List<ListAmFlatAdInfo> {
        return httpClient
            .get {
                url("/en/category/56/$pageNum")
                header("Cookie", "gl=2;")
                header("Host", "www.list.am")
            }
            .body()
    }

    suspend fun fetchAd(id: String): ListAmFlatAd {
        return httpClient
            .get("/ru/item/$id")
            .body()
    }

}
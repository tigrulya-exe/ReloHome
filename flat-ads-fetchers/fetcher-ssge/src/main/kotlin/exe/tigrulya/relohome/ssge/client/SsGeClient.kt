package exe.tigrulya.relohome.ssge.client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import exe.tigrulya.relohome.fetcher.ExternalParseableSiteClient
import exe.tigrulya.relohome.fetcher.HtmlDomParser
import exe.tigrulya.relohome.fetcher.domConverter
import exe.tigrulya.relohome.ssge.model.*
import exe.tigrulya.relohome.ssge.parser.SsGeDomParser
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*

class SsGeClient(
    baseUrl: String = "https://api-gateway.ss.ge/v1/"
) : ExternalParseableSiteClient<Any>(baseUrl) {

    companion object {
        const val SS_SESSION_TOKEN_KEY = "ss-session-token"
    }

    private var sessionToken: String? = null

    override fun configureHttpClient(config: HttpClientConfig<CIOEngineConfig>) {
        config.install(ContentNegotiation) {
            domConverter(htmlDomParser(), baseUrl)
            jackson {
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                registerModule(JavaTimeModule())
            }
        }
        config.install(Logging) {
            level = LogLevel.NONE
        }
    }

    override fun htmlDomParser(): HtmlDomParser<Any> = SsGeDomParser()

    suspend fun fetchAdInfos(request: GetSsGeFlatAdsRequest): List<SsGeFlatAdInfo> {
        // TODO encapsulate auth token reacquire logic in client
        val authToken = sessionToken ?: updateSessionToken()
        refreshAccessToken()
        val flatsContainer: SsGeFlatAdsContainer = httpClient
            .post {
                url("RealEstate/LegendSearch")
                contentType(ContentType.Application.Json)
                header("Accept-Language", "en")
                header("Host", "api-gateway.ss.ge")
                bearerAuth(authToken)
                setBody(request)
            }
            .body()
        return flatsContainer.realStateItemModel
    }

    suspend fun fetchAd(detailUrl: String): SsGeFlatAdContainer {
        val authToken = sessionToken ?: updateSessionToken()
        return httpClient
            .get {
                url("https://home.ss.ge/en/real-estate/$detailUrl")
                contentType(ContentType.Text.Html)
                header("Accept-Language", "en")
                header("Host", "home.ss.ge")
                bearerAuth(authToken)
            }

            .body()
    }

    suspend fun refreshAccessToken() {
        val authToken = sessionToken ?: updateSessionToken()
        httpClient
            .get {
                url("https://home.ss.ge/api/refresh_access_token")
                contentType(ContentType.Text.Html)
                header("Accept-Language", "en")
                header("Host", "home.ss.ge")
                bearerAuth(authToken)
            }
    }

    suspend fun translate(text: String, targetLanguage: TranslateLanguage): String {
        val authToken = sessionToken ?: updateSessionToken()
        return httpClient
            .post {
                url("https://home.ss.ge/api/translateApp")
                contentType(ContentType.Application.Json)
                header("Accept-Language", "en")
                header("Host", "home.ss.ge")
                header("Origin", "https://home.ss.ge")
                bearerAuth(authToken)
                setBody(TranslateRequest(text, targetLanguage))
            }
            .body<TranslateResponse>()
            .translation.also { println("translated: $it") }
    }

    override fun onAuthError(errorMessage: String) {
        sessionToken = null
        super.onAuthError(errorMessage)
    }

    private suspend fun updateSessionToken(): String {
        val serverCookies = httpClient.get("https://home.ss.ge/en/real-estate")
            .setCookie()

        return serverCookies[SS_SESSION_TOKEN_KEY]?.value?.apply {
            sessionToken = this
        } ?: throw IllegalStateException("Can't find session token '$SS_SESSION_TOKEN_KEY' in cookies: $serverCookies")
    }
}
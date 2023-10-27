package exe.tigrulya.relohome.ssge.client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import exe.tigrulya.relohome.connector.ExternalSiteClient
import exe.tigrulya.relohome.ssge.model.GetSsGeFlatAdsRequest
import exe.tigrulya.relohome.ssge.model.SsGeFlatAd
import exe.tigrulya.relohome.ssge.model.SsGeFlatAdsContainer
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
) : ExternalSiteClient<Any>(baseUrl) {

    companion object {
        const val SS_SESSION_TOKEN_KEY = "ss-session-token"
    }

    private var sessionToken: String? = null
    override fun configureHttpClient(config: HttpClientConfig<CIOEngineConfig>) {
        config.install(ContentNegotiation) {
            jackson {
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                registerModule(JavaTimeModule())
            }
        }
        config.install(Logging) {
            level = LogLevel.NONE
        }
    }

    suspend fun fetchAds(request: GetSsGeFlatAdsRequest): List<SsGeFlatAd> {
        // TODO encapsulate auth token reacquire logic in client
        val authToken = sessionToken ?: getSessionToken()
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

    private suspend fun getSessionToken(): String {
        val serverCookies = httpClient.get("https://home.ss.ge/en/real-estate")
            .setCookie()
        // TODO lol
        return serverCookies[SS_SESSION_TOKEN_KEY]!!.value
    }
}
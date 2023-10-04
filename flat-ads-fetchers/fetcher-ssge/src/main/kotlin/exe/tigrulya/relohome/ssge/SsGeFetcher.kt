package exe.tigrulya.relohome.ssge

import exe.tigrulya.relohome.connector.AbstractExternalFetcher
import exe.tigrulya.relohome.connector.FlatAdMapper
import exe.tigrulya.relohome.connector.LastHandledAdTimestampProvider
import exe.tigrulya.relohome.connector.WindowTillNowTimestampProvider
import exe.tigrulya.relohome.connector.model.City
import exe.tigrulya.relohome.connector.model.FlatAd
import exe.tigrulya.relohome.connector.util.LoggerProperty
import exe.tigrulya.relohome.ssge.client.SsGeClient
import exe.tigrulya.relohome.ssge.model.GetSsGeFlatAdsRequest
import exe.tigrulya.relohome.ssge.model.SsGeFlatAd
import kotlinx.coroutines.flow.FlowCollector
import java.time.Instant
import java.time.temporal.ChronoUnit

object SsGeFlatAdMapper : FlatAdMapper<SsGeFlatAd> {
    override fun toFlatAd(externalFlatAd: SsGeFlatAd) = FlatAd(
        id = externalFlatAd.applicationId.toString(),
        price = externalFlatAd.price.priceUsd.toString(),
        description = """
            New flat from SS.GE: ${externalFlatAd.applicationId}, price: ${externalFlatAd.price.priceGeo}
            ${externalFlatAd.description}
        """.trimIndent(),
        // TODO
        city = City(
            name = "Tbilisi",
            country = "Georgis"
        )
    )
}

class SsGeFetcher(
    baseUrl: String = "https://api-gateway.ss.ge/v1/",
    lastHandledAdTimestampProvider: LastHandledAdTimestampProvider
    = WindowTillNowTimestampProvider(10, ChronoUnit.MINUTES)
) : AbstractExternalFetcher<SsGeFlatAd>(lastHandledAdTimestampProvider) {
    private val client = SsGeClient(baseUrl)
    private lateinit var lastHandledPageAdTime: Instant
    private val logger by LoggerProperty()

    override suspend fun fetchPage(collector: FlowCollector<SsGeFlatAd>, page: Int): FetchResult {
        lastHandledPageAdTime = lastHandledAdTime
        val ads = client.fetchAds(
            GetSsGeFlatAdsRequest(page = page)
        )

        val unseenAds = ads
            .filter { it.orderDate > lastHandledAdTime }
            .onEach { lastHandledPageAdTime = maxOf(lastHandledPageAdTime, it.orderDate) }
            .map { collector.emit(it) }

        logger.info("Fetched ${unseenAds.size} ads")
        return if (unseenAds.size != ads.size) {
            FetchResult.NextPageRequired
        } else {
            FetchResult.Completed(lastHandledPageAdTime)
        }
    }
}
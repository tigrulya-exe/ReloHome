package exe.tigrulya.relohome.ssge

import exe.tigrulya.relohome.connector.AbstractExternalConnector
import exe.tigrulya.relohome.connector.FlatAdMapper
import exe.tigrulya.relohome.connector.LastHandledAdTimestampProvider
import exe.tigrulya.relohome.connector.WindowTillNowTimestampProvider
import exe.tigrulya.relohome.ssge.client.SsGeClient
import exe.tigrulya.relohome.connector.model.City
import exe.tigrulya.relohome.connector.model.FlatAd
import exe.tigrulya.relohome.ssge.model.GetSsGeFlatAdsRequest
import exe.tigrulya.relohome.ssge.model.SsGeFlatAd
import kotlinx.coroutines.flow.FlowCollector
import java.time.Instant
import java.time.temporal.ChronoUnit

object SsGeFlatAdMapper : FlatAdMapper<SsGeFlatAd> {
    override fun toFlatAd(externalFlatAd: SsGeFlatAd) = FlatAd(
        id = externalFlatAd.applicationId.toString(),
        price = externalFlatAd.price.priceUsd.toString(),
        description = "New flat: ${externalFlatAd.applicationId} - price ${externalFlatAd.price.priceGeo}",
        // TODO
        city = City(
            name = "Tbilisi",
            country = "Georgis"
        )
    )
}

class SsGeFetcher(
    private val baseUrl: String = "https://api-gateway.ss.ge/v1/",
    private val lastHandledAdTimestampProvider: LastHandledAdTimestampProvider
    = WindowTillNowTimestampProvider(2, ChronoUnit.MINUTES)
) : AbstractExternalConnector<SsGeFlatAd>() {
    private val client = SsGeClient(baseUrl)

    override suspend fun fetchBatch(collector: FlowCollector<SsGeFlatAd>) {
        var pageNum = 1

        // used just for testing
        val lastHandledAdTime = lastHandledAdTimestampProvider.provide() ?: Instant.now()
        do {
            val ads = client.fetchAds(
                GetSsGeFlatAdsRequest(page = pageNum))
            val unseenAds = ads
                .filter { it.orderDate.toInstant() > lastHandledAdTime }
                .map { collector.emit(it) }

            ++pageNum
        } while (unseenAds.size == ads.size)
    }
}
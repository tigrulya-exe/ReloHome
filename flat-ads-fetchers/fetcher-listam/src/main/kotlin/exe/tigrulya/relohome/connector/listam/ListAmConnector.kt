package exe.tigrulya.relohome.connector.listam

import exe.tigrulya.relohome.connector.AbstractExternalConnector
import exe.tigrulya.relohome.connector.ExternalConnector
import exe.tigrulya.relohome.connector.FlatAdMapper
import exe.tigrulya.relohome.connector.LastHandledAdTimestampProvider
import exe.tigrulya.relohome.connector.WindowTillNowTimestampProvider
import exe.tigrulya.relohome.connector.listam.fetcher.ListAmClient
import exe.tigrulya.relohome.connector.listam.parser.ListAmFlatAd
import exe.tigrulya.relohome.connector.model.City
import exe.tigrulya.relohome.connector.model.FlatAd
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.temporal.ChronoUnit

object ListAmFlatAdMapper : FlatAdMapper<ListAmFlatAd> {
    override fun toFlatAd(externalFlatAd: ListAmFlatAd) = FlatAd(
        id = externalFlatAd.id,
        price = externalFlatAd.price ?: "Price unknown",
        description = "New flat: ${externalFlatAd.id} - price ${externalFlatAd.price}",
        city = City(
            name = "Yerevan",
            country = "Armenia"
        )
    )

}

class ListAmConnector(
    private val baseUrl: String = "https://www.list.am",
    private val lastHandledAdTimestampProvider: LastHandledAdTimestampProvider
    = WindowTillNowTimestampProvider(2, ChronoUnit.MINUTES)
) : AbstractExternalConnector<ListAmFlatAd>() {
    private val client = ListAmClient(baseUrl)

    override suspend fun fetchBatch(collector: FlowCollector<ListAmFlatAd>) {
        var pageNum = 1

        // used just for testing

        val lastHandledAdTime = lastHandledAdTimestampProvider.provide() ?: Instant.now()
        do {
            val ads = client.fetchAds(pageNum)
            val unseenAds = ads
                .filter { it.lastModified > lastHandledAdTime }
                .map { it.id }
                .map { client.fetchAd(it) }
                .map { collector.emit(it) }

            ++pageNum
            // TODO: send mapped ads to core service via kafka
        } while (unseenAds.size == ads.size)
    }
}
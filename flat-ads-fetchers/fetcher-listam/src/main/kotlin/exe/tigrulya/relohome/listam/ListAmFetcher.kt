package exe.tigrulya.relohome.listam

import exe.tigrulya.relohome.fetcher.AbstractExternalFetcher
import exe.tigrulya.relohome.fetcher.FlatAdMapper
import exe.tigrulya.relohome.fetcher.LastHandledAdTimestampProvider
import exe.tigrulya.relohome.fetcher.WindowTillNowTimestampProvider
import exe.tigrulya.relohome.listam.client.ListAmClient
import exe.tigrulya.relohome.listam.parser.ListAmFlatAd
import exe.tigrulya.relohome.fetcher.model.City
import exe.tigrulya.relohome.fetcher.model.FlatAd
import kotlinx.coroutines.flow.FlowCollector
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

class ListAmFetcher(
    baseUrl: String = "https://www.list.am",
    lastHandledAdTimestampProvider: LastHandledAdTimestampProvider
    = WindowTillNowTimestampProvider(2, ChronoUnit.MINUTES)
) : AbstractExternalFetcher<ListAmFlatAd>(lastHandledAdTimestampProvider) {
    private val client = ListAmClient(baseUrl)
    private lateinit var lastHandledPageAdTime: Instant

    override suspend fun fetchPage(collector: FlowCollector<ListAmFlatAd>, page: Int): FetchResult {
        lastHandledPageAdTime = lastHandledAdTime
        val ads = client.fetchAds(page)

        val unseenAds = ads
            .filter { it.lastModified > lastHandledAdTime }
            .map { it.id }
            .map { client.fetchAd(it) }
            //TODO .onEach { update lastHandledPageAdTime }
            .map { collector.emit(it) }

        return if (unseenAds.size != ads.size) {
            FetchResult.NextPageRequired(lastHandledPageAdTime)
        } else {
            FetchResult.Completed(lastHandledPageAdTime)
        }
    }
}
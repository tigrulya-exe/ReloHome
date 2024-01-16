package exe.tigrulya.relohome.listam

import exe.tigrulya.relohome.fetcher.AbstractExternalFetcher
import exe.tigrulya.relohome.fetcher.FlatAdMapper
import exe.tigrulya.relohome.fetcher.LastHandledAdTimestampProvider
import exe.tigrulya.relohome.listam.client.ListAmClient
import exe.tigrulya.relohome.listam.parser.ListAmFlatAd
import exe.tigrulya.relohome.model.*
import kotlinx.coroutines.flow.FlowCollector
import java.time.Instant

object ListAmFlatAdMapper : FlatAdMapper<ListAmFlatAd> {
    override fun toFlatAd(externalFlatAd: ListAmFlatAd) = FlatAd(
        id = externalFlatAd.id,
        title = "TODO",
        description = "New flat: ${externalFlatAd.id} - price ${externalFlatAd.price}",
        serviceId = ListAmFetcher.FETCHER_ID,
        address = Address(
            city = City(
                name = "Yerevan",
                country = "Armenia"
            )
        ),
        info = FlatInfo(
            floor = -1
        ),
        contacts = Contacts(
            flatServiceLink = ""
        )
    )
}

class ListAmFetcher(
    baseUrl: String = "https://www.list.am",
    lastHandledAdTimestampProvider: LastHandledAdTimestampProvider
) : AbstractExternalFetcher<ListAmFlatAd>(lastHandledAdTimestampProvider) {
    companion object {
        const val FETCHER_ID = "list.am"
    }

    private val client = ListAmClient(baseUrl)

    override suspend fun fetchPage(
        collector: FlowCollector<ListAmFlatAd>,
        page: Int,
        lastHandledAdTime: Instant
    ): FetchResult {
        var lastHandledPageAdTime = lastHandledAdTime
        val ads = client.fetchAds(page)

        val unseenAds = ads
            .filter { it.lastModified > lastHandledAdTime }
            .onEach { lastHandledPageAdTime = maxOf(lastHandledPageAdTime, it.lastModified) }
            .map { client.fetchAd(it.id) }
            .map { collector.emit(it) }

        return if (unseenAds.size == ads.size) {
            FetchResult.NextPageRequired(lastHandledPageAdTime)
        } else {
            FetchResult.Completed(lastHandledPageAdTime)
        }
    }

    override fun flatAdMapper(): FlatAdMapper<ListAmFlatAd> = ListAmFlatAdMapper
}
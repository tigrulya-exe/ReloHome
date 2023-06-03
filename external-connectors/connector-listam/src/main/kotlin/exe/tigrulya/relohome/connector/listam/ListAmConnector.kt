package exe.tigrulya.relohome.connector.listam

import exe.tigrulya.relohome.connector.LastHandledAdTimestampProvider
import exe.tigrulya.relohome.connector.WindowTillNowTimestampProvider
import exe.tigrulya.relohome.connector.listam.fetcher.ListAmClient
import exe.tigrulya.relohome.connector.listam.parser.ListAmFlatAd
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.temporal.ChronoUnit

class ListAmConnector(
    private val baseUrl: String = "https://www.list.am",
    private val lastHandledAdTimestampProvider: LastHandledAdTimestampProvider
    = WindowTillNowTimestampProvider(2, ChronoUnit.MINUTES)
) {
    private val client = ListAmClient(baseUrl)

    fun fetch(): Flow<ListAmFlatAd> = flow {
        var pageNum = 1

        // used just for testing

        val lastHandledAdTime = lastHandledAdTimestampProvider.provide() ?: Instant.now()
        do {
            val ads = client.fetchAds(pageNum)
            val unseenAds = ads
                .filter { it.lastModified > lastHandledAdTime }
                .map { it.id }
                .map { client.fetchAd(it) }
                .map { emit(it) }

            ++pageNum
            // TODO: send mapped ads to core service via kafka
        } while (unseenAds.size == ads.size)
    }
}
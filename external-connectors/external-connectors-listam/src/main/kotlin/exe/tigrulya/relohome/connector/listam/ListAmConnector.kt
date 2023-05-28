package exe.tigrulya.relohome.connector.listam

import exe.tigrulya.relohome.connector.listam.fetcher.ListAmClient
import exe.tigrulya.relohome.connector.listam.parser.ListAmFlatAd
import java.time.Instant
import java.time.temporal.ChronoUnit

class ListAmConnector(
    private val baseUrl: String = "https://www.list.am"
) {
    private val client = ListAmClient(baseUrl)

    suspend fun fetch(): List<ListAmFlatAd> {
        var pageNum = 1

        // used just for testing
        val flatAds = mutableListOf<ListAmFlatAd>()

        val lastHandledAdTime = getLastHandledAdTime()
        do {
            val ads = client.fetchAds(pageNum)
            val unseenAds = ads
                .filter { it.lastModified > lastHandledAdTime }
                .map { it.id }
                .map { client.fetchAd(it) }

            ++pageNum
            flatAds.addAll(unseenAds)
            // TODO: send mapped ads to core service via kafka
        } while (unseenAds.size == ads.size)

        return flatAds
    }

    private fun getLastHandledAdTime(): Instant {
        return Instant.now()
            .minus(10, ChronoUnit.MINUTES)
    }
}
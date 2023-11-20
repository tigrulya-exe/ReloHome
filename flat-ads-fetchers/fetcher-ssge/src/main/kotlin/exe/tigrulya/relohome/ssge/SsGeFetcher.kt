package exe.tigrulya.relohome.ssge

import exe.tigrulya.relohome.fetcher.AbstractExternalFetcher
import exe.tigrulya.relohome.fetcher.LastHandledAdTimestampProvider
import exe.tigrulya.relohome.fetcher.WindowTillNowTimestampProvider
import exe.tigrulya.relohome.util.LoggerProperty
import exe.tigrulya.relohome.ssge.client.SsGeClient
import exe.tigrulya.relohome.ssge.model.GetSsGeFlatAdsRequest
import exe.tigrulya.relohome.ssge.model.SsGeFlatAd
import kotlinx.coroutines.flow.FlowCollector
import java.time.Instant
import java.time.temporal.ChronoUnit

class SsGeFetcher(
    baseUrl: String = "https://api-gateway.ss.ge/v1/",
    lastHandledAdTimestampProvider: LastHandledAdTimestampProvider
    = WindowTillNowTimestampProvider(10, ChronoUnit.MINUTES)
) : AbstractExternalFetcher<SsGeFlatAd>(lastHandledAdTimestampProvider) {
    companion object {
        const val FETCHER_ID = "ss.ge"
    }

    private val logger by LoggerProperty()

    private val client = SsGeClient(baseUrl)
    private lateinit var lastHandledPageAdTime: Instant

    override suspend fun fetchPage(collector: FlowCollector<SsGeFlatAd>, page: Int): FetchResult {
        lastHandledPageAdTime = lastHandledAdTime
        val ads = client.fetchAds(
            GetSsGeFlatAdsRequest(page = page)
        )

        val unseenAds = ads
            .filter { it.orderDate > lastHandledAdTime }
            .onEach { lastHandledPageAdTime = maxOf(lastHandledPageAdTime, it.orderDate) }
            .map { collector.emit(it) }

        logger.info("Fetched ${unseenAds.size} ads from page $page")
        // ss.ge use to put some "premium" ads on the first page
        return if (page == 1 || unseenAds.size == ads.size) {
            FetchResult.NextPageRequired(lastHandledPageAdTime)
        } else {
            FetchResult.Completed(lastHandledPageAdTime)
        }
    }
}
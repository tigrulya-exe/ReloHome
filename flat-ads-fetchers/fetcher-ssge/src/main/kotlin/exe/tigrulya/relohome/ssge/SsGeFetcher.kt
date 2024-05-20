package exe.tigrulya.relohome.ssge

import exe.tigrulya.relohome.fetcher.AbstractExternalFetcher
import exe.tigrulya.relohome.fetcher.FlatAdMapper
import exe.tigrulya.relohome.fetcher.LastHandledAdTimestampProvider
import exe.tigrulya.relohome.util.LoggerProperty
import exe.tigrulya.relohome.ssge.client.SsGeClient
import exe.tigrulya.relohome.ssge.model.GetSsGeFlatAdsRequest
import exe.tigrulya.relohome.ssge.model.SsGeFlatAdContainer
import kotlinx.coroutines.flow.*
import java.time.Instant

class SsGeFetcher(
    baseUrl: String = "https://api-gateway.ss.ge/v1/",
    lastHandledAdTimestampProvider: LastHandledAdTimestampProvider,
    private val asyncBufferCapacity: Int = 10
) : AbstractExternalFetcher<SsGeFlatAdContainer>(lastHandledAdTimestampProvider) {
    companion object {
        const val FETCHER_ID = "ss.ge"
    }

    private val logger by LoggerProperty()

    private val client = SsGeClient(baseUrl)

    override suspend fun fetchPage(
        collector: FlowCollector<SsGeFlatAdContainer>,
        page: Int,
        lastHandledAdTime: Instant
    ): FetchResult {
        var lastHandledPageAdTime = lastHandledAdTime
        val ads = client.fetchAdInfos(
            GetSsGeFlatAdsRequest(page = page)
        )

        val unseenAdsCount = ads
            .asFlow()
            .filter { it.orderDate > lastHandledAdTime }
            .onEach { lastHandledPageAdTime = maxOf(lastHandledPageAdTime, it.orderDate) }
            .map { client.fetchAd(it.detailUrl) }
            // todo move it to base fetcher
            .buffer(asyncBufferCapacity)
            .map { collector.emit(it) }
            .count()

        logger.info("Fetched $unseenAdsCount ads from page $page")
        // ss.ge use to put some "premium" ads on the first page
        return if (page == 1 || unseenAdsCount == ads.size) {
            FetchResult.NextPageRequired(lastHandledPageAdTime)
        } else {
            FetchResult.Completed(lastHandledPageAdTime)
        }
    }

    override fun flatAdMapper(): FlatAdMapper<SsGeFlatAdContainer> = SsGeFlatAdMapper()
}
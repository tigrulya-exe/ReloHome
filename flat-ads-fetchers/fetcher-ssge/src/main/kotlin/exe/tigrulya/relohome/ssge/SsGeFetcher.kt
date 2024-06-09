package exe.tigrulya.relohome.ssge

import exe.tigrulya.relohome.fetcher.AbstractExternalFetcher
import exe.tigrulya.relohome.fetcher.FlatAdMapper
import exe.tigrulya.relohome.fetcher.LastHandledAdTimestampProvider
import exe.tigrulya.relohome.ssge.client.SsGeClient
import exe.tigrulya.relohome.ssge.model.GetSsGeFlatAdsRequest
import exe.tigrulya.relohome.ssge.model.SsGeFlatAdContainer
import exe.tigrulya.relohome.ssge.model.TranslateLanguage
import exe.tigrulya.relohome.util.LoggerProperty
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.map
import java.time.Instant

class SsGeFetcher(
    baseUrl: String = "https://api-gateway.ss.ge/v1/",
    lastHandledAdTimestampProvider: LastHandledAdTimestampProvider,
    private val maxImagesPerAd: Int = 8
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
            .filter { it.orderDate > lastHandledAdTime }
            .onEach { lastHandledPageAdTime = maxOf(lastHandledPageAdTime, it.orderDate) }
            .asFlow()
            .mapWithFailover { client.fetchAd(it.detailUrl) }
            .mapWithFailover { maybeTranslateDescription(it) }
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

    private suspend fun maybeTranslateDescription(flatAd: SsGeFlatAdContainer): SsGeFlatAdContainer {
        return flatAd.apply {
            applicationData.description.apply {
                text = text
                    ?: ka?.let { client.translate(it, TranslateLanguage.ENG) }
            }
        }
    }

    override fun flatAdMapper(): FlatAdMapper<SsGeFlatAdContainer> = SsGeFlatAdMapper(maxImagesPerAd)
}
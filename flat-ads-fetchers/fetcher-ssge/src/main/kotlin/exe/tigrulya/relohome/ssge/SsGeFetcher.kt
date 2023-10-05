package exe.tigrulya.relohome.ssge

import exe.tigrulya.relohome.connector.AbstractExternalFetcher
import exe.tigrulya.relohome.connector.FlatAdMapper
import exe.tigrulya.relohome.connector.LastHandledAdTimestampProvider
import exe.tigrulya.relohome.connector.WindowTillNowTimestampProvider
import exe.tigrulya.relohome.connector.model.*
import exe.tigrulya.relohome.connector.util.LoggerProperty
import exe.tigrulya.relohome.ssge.client.SsGeClient
import exe.tigrulya.relohome.ssge.model.GetSsGeFlatAdsRequest
import exe.tigrulya.relohome.ssge.model.SsGeFlatAd
import kotlinx.coroutines.flow.FlowCollector
import java.time.Instant
import java.time.temporal.ChronoUnit

object SsGeFlatAdMapper : FlatAdMapper<SsGeFlatAd> {
    override fun toFlatAd(externalFlatAd: SsGeFlatAd): FlatAd {
        val address = with(externalFlatAd.address) {
            Address(
                city = City(
                    name = "Tbilisi",
                    country = "Georgis"
                ),
                district = districtTitle,
                subDistrict = subdistrictTitle,
                street = streetTitle,
                //TODO add here location parse from gmaps from description
            )
        }

        val price = with(externalFlatAd.price) {
            Price(
                amount = this.priceUsd,
                currency = Price.Currency.USD
            )
        }

        val flatInfo = FlatInfo(

        )

        val contacts = Contacts(

        )

        val pictures = listOf<Picture>()

        return FlatAd(
            id = externalFlatAd.applicationId.toString(),
            title = externalFlatAd.title,
            address = address,
            price = price,
            description = externalFlatAd.description,
            info = flatInfo,
            contacts = contacts,
            serviceId = SsGeFetcher.FETCHER_ID,
            pictures = pictures
        )
    }
}

class SsGeFetcher(
    baseUrl: String = "https://api-gateway.ss.ge/v1/",
    lastHandledAdTimestampProvider: LastHandledAdTimestampProvider
    = WindowTillNowTimestampProvider(10, ChronoUnit.MINUTES)
) : AbstractExternalFetcher<SsGeFlatAd>(lastHandledAdTimestampProvider) {
    companion object {
        const val FETCHER_ID = "ss.ge"
    }

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
package exe.tigrulya.relohome.ssge.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonValue
import java.time.Instant

enum class EstateType(@get:JsonValue val id: Int) {
    APARTMENT(5),
    HOUSE(2)
}

enum class DealType(@get:JsonValue val id: Int) {
    RENT(1)
}

enum class Currency(@get:JsonValue @set:JsonCreator var id: Int) {
    GEL(1),
    USD(2)
}

enum class Order(@get:JsonValue @set:JsonCreator var id: Int) {
    DATE_DESCENDING(1),
}

data class GetSsGeFlatAdsRequest(
    val realEstateType: EstateType = EstateType.APARTMENT,
    val realEstateDealType: DealType = DealType.RENT,
    val cityIdList: List<City> = listOf(City.TBILISI),
    val pageSize: Int = 30,
    val page: Int = 1,
    val currencyId: Currency = Currency.GEL,
    val order: Order = Order.DATE_DESCENDING
)

data class FlatAdImageContainer(
    // https://static.ss.ge/20230906/12_c226770a-a5a9-4daa-8b4a-44bb5943049f.jpg
    val fileName: String,
    val orderNo: Int
)

data class FlatPrice(
    val priceUsd: Int,
    val priceGeo: Int?,
    val currencyType: Currency?
)

data class ApplicationPhone(
    val phoneNumber: String?,
    val hasViber: Boolean?,
    val hasWhatsapp: Boolean?
)

data class SsGeFlatAdInfo(
    val detailUrl: String,
    @JsonFormat(
        pattern = "yyyy-MM-dd'T'HH:mm:ss.[SSSSSSS][SSSSSS][SSSSS][SSSS][SSS][XXX]",
        timezone = "Asia/Tbilisi"
    )
    val orderDate: Instant
)

data class Description(
    // original string
    val text: String?,
    val en: String?,
    val ka: String?,
    val ru: String?
)

// TODO check non-nullable fields
data class SsGeFlatAdContainer(
    val applicationData: SsGeFlatAd,
    val fullUrl: String
)

data class SsGeFlatAd(
    // 7661118
    val applicationId: Int,
    val address: SsGeLocation,
    val appImages: List<FlatAdImageContainer>?,
    val description: Description,
    val floor: String?,
    val floors: String,
    val bedrooms: Int,
    val rooms: Int,
    // meh, shitty format from backend
    @JsonFormat(
        pattern = "yyyy-MM-dd'T'HH:mm:ss.[SSSSSSS][SSSSSS][SSSSS][SSSS][SSS][XXX]",
        timezone = "Asia/Tbilisi"
    )
    val orderDate: Instant,
    val title: String,
    val totalArea: Double,
    val price: FlatPrice,
    val applicationPhones: List<ApplicationPhone>,
    val locationLatitude: String?,
    val locationLongitude: String?
)

data class SsGeFlatAdsContainer(
    val realStateItemModel: List<SsGeFlatAdInfo>
)
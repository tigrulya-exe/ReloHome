package exe.tigrulya.relohome.ssge.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.time.ZonedDateTime

enum class EstateType(@get:JsonValue val id: Int) {
    APARTMENT(5),
    HOUSE(2)
}

enum class DealType (@get:JsonValue val id: Int) {
    RENT(1)
}

enum class Currency(@get:JsonValue @set:JsonCreator var id: Int) {
    GEL(1),
    USD(2)
}

data class GetSsGeFlatAdsRequest(
    val realEstateType: EstateType = EstateType.APARTMENT,
    val realEstateDealType: DealType = DealType.RENT,
    val cityIdList: List<City> = listOf(City.TBILISI),
    val pageSize: Int = 50,
    val page: Int = 1,
    val currencyId: Currency = Currency.GEL
)

data class FlatAdImageContainer(
    // https://static.ss.ge/20230906/12_c226770a-a5a9-4daa-8b4a-44bb5943049f_Thumb.jpg
    val fileName: String,
    val orderNo: Int
)

data class FlatPrice(
    val priceGeo: Int,
    val priceUsd: Int,
    val currencyType: Currency
)

// TODO check non-nullable fields
data class SsGeFlatAd(
    // 7661118
    val applicationId: Int,
    val address: SsGeLocation,
    val appImages: List<FlatAdImageContainer>?,
    // 2023-09-06T16:18:55.52278+04:00
    val createDate: ZonedDateTime,
    val description: String?,
    // "2-room-flat-for-rent-didi-digomi-7661118"
    val detail: String?,
    val floorNumber: String,
    val totalAmountOfFloor: Int,
    val numberOfBedrooms: Int,
    // 2023-10-03T20:36:50.0475696+04:00
    val orderDate: ZonedDateTime,
    val title: String,
    val totalArea: Int,
    val price: FlatPrice
)

data class SsGeFlatAdsContainer(
    val realStateItemModel: List<SsGeFlatAd>
)
package exe.tigrulya.relohome.ssge.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Feature
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.annotation.OptBoolean
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Local

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
    val priceGeo: Int?,
    val priceUsd: Int?,
    val currencyType: Currency?
)

// TODO check non-nullable fields
data class SsGeFlatAd(
    // 7661118
    val applicationId: Int,
    val address: SsGeLocation,
    val appImages: List<FlatAdImageContainer>?,
    val createDate: Instant,
    val description: String?,
    // "2-room-flat-for-rent-didi-digomi-7661118"
    val detail: String?,
    val floorNumber: String,
    val totalAmountOfFloor: Int,
    val numberOfBedrooms: Int,
    // meh, shitty format from backend
    @JsonFormat(
        pattern = "yyyy-MM-dd'T'HH:mm:ss.[SSSSSSS][SSSSSS][SSSSS][SSSS][XXX]",
        timezone = "Asia/Tbilisi"
    )
    val orderDate: Instant,
    val title: String,
    val totalArea: Int,
    val price: FlatPrice
)

data class SsGeFlatAdsContainer(
    val realStateItemModel: List<SsGeFlatAd>
)
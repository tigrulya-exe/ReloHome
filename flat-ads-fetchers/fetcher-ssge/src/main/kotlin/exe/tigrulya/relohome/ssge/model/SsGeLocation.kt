package exe.tigrulya.relohome.ssge.model

import com.fasterxml.jackson.annotation.JsonValue

data class SsGeLocation(
    val cityId: Int,
    val cityTitle: String,
    val districtId: Int?,
    val districtTitle: String?,
    val municipalityId: Int?,
    val municipalityTitle: String?,
    val streetId: Int?,
    val streetNumber: String?,
    val streetTitle: String?,
    val subdistrictId: Int?,
    val subdistrictTitle: String?
)

enum class City(@get:JsonValue val id: Int) {
    TBILISI(95)
}
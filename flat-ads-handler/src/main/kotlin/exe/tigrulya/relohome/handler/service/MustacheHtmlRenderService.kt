package exe.tigrulya.relohome.handler.service

import exe.tigrulya.relohome.model.NumRange
import exe.tigrulya.relohome.model.UserSearchOptionsInfo
import io.ktor.server.mustache.*

class MustacheHtmlRenderService {
    fun renderForm(formId: String, searchOptions: UserSearchOptionsInfo, allDistricts: List<String>): MustacheContent {
        return MustacheContent(
            "${formId}.hbs",
            mapOf("searchData" to MustacheFormData(searchOptions, allDistricts))
        )
    }
}

data class TemplateDistrictOption(val name: String, val selected: Boolean)

// todo replace with list of scope objects
class MustacheFormData(formData: UserSearchOptionsInfo, allDistricts: List<String>) {
    val priceRange: NumRange = formData.priceRange
    val roomRange: NumRange = formData.roomRange
    val bedroomRange: NumRange = formData.bedroomRange
    val floorRange: NumRange = formData.floorRange
    val areaRange: NumRange = formData.areaRange
    val cityName: String = formData.cityName
    val subDistricts: List<TemplateDistrictOption> = allDistricts.map {
        TemplateDistrictOption(it, formData.subDistricts.contains(it))
    }
}
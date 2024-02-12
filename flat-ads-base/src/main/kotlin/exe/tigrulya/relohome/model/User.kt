package exe.tigrulya.relohome.model

enum class UserState {
    NEW,
    CITY_PROVIDED,
    SEARCH_OPTIONS_PROVIDED,
    SUBSCRIPTION_PURCHASED;

    fun canSetSearchOptions() = this > NEW

    fun searchOptionsProvided() = this > CITY_PROVIDED
}

data class UserCreateDto(
    val name: String,
    val externalId: String
)

data class User(
    val id: Long,
    val name: String,
    val externalId: String,
    val state: UserState,
    // todo tmp
    val city: City?
)

data class NumRange(val from: Int? = null, val to: Int? = null)

data class UserSearchOptionsDto(
    val priceRange: NumRange,
    val roomRange: NumRange,
    val subDistricts: List<String>,
    val enabled: Boolean = true
)

data class UserSearchOptionsInfo(
    val priceRange: NumRange = NumRange(),
    val roomRange: NumRange = NumRange(),
    val cityName: String,
    val subDistricts: Set<String> = setOf()
)
package exe.tigrulya.relohome.model

enum class UserState {
    NEW,
    CITY_PROVIDED,
    SEARCH_OPTIONS_PROVIDED;

    fun canSetSearchOptions() = this != NEW
}

data class UserCreateDto(
    val name: String,
    val externalId: String
)

data class User(
    val id: Long,
    val name: String,
    val externalId: String,
    val state: UserState
)

data class NumRange(val from: Int?, val to: Int?)

data class UserSearchOptionsDto(
    val priceRange: NumRange,
    val roomRange: NumRange,
    val subDistricts: List<String>
)
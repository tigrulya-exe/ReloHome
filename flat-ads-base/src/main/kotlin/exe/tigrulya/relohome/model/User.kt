package exe.tigrulya.relohome.model

enum class UserState {
    NEW,
    CITY_PROVIDED,
    SEARCH_OPTIONS_PROVIDED
}

data class UserCreateDto(
    val id: Long,
    val name: String,
    val externalId: String
)

data class User(
    val id: Long,
    val name: String,
    val externalId: String,
    val state: UserState
)

data class UserSearchOptions(
    val city: City
)
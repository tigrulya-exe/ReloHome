package exe.tigrulya.relohome.handler.model

import exe.tigrulya.relohome.connector.model.City

enum class UserState {
    NEW,
    CITY_PROVIDED,
    SEARCH_OPTIONS_PROVIDED
}

data class UserCreateDto(
    val id: Long,
    val name: String
)

data class User(
    val id: Long,
    val name: String,
    val state: UserState
)

data class UserSearchOptions(
    val city: City
)
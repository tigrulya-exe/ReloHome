package exe.tigrulya.relohome.api.user_handler

import exe.tigrulya.relohome.model.*


interface UserHandlerGateway {
    suspend fun registerUser(user: UserCreateDto)

    suspend fun setLocation(externalId: String, city: City)

    // todo locale as enum or class
    suspend fun setLocale(externalId: String, locale: String)

    suspend fun setSearchOptions(externalUserId: String, searchOptions: UserSearchOptionsDto)

    suspend fun toggleSearch(externalId: String): Boolean

    suspend fun getUserInfo(externalId: String): UserInfo
}

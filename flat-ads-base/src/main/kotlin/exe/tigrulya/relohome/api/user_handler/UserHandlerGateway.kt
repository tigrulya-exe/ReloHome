package exe.tigrulya.relohome.api.user_handler

import exe.tigrulya.relohome.model.City
import exe.tigrulya.relohome.model.UserCreateDto
import exe.tigrulya.relohome.model.UserSearchOptionsDto


interface UserHandlerGateway {
    suspend fun registerUser(user: UserCreateDto)

    suspend fun setLocation(externalId: String, city: City)

    suspend fun setSearchOptions(externalUserId: String, searchOptions: UserSearchOptionsDto)

    suspend fun toggleSearch(externalId: String): Boolean
}

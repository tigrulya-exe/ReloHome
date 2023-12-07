package exe.tigrulya.relohome.handler.service

import exe.tigrulya.relohome.handler.repository.Cities
import exe.tigrulya.relohome.handler.repository.UserEntity
import exe.tigrulya.relohome.handler.repository.UserSearchOptions
import exe.tigrulya.relohome.handler.repository.Users
import exe.tigrulya.relohome.model.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserService {
    fun registerUser(user: UserCreateDto) {
        UserEntity.new {
            name = user.name
            externalId = externalId
            state = UserState.NEW
        }
    }

    fun setLocation(externalId: String, city: City) {
        transaction {
            val userEntity = Users.getByExternalId(externalId)
            val cityEntity = Cities.getByName(city.name)

            userEntity.location = cityEntity
            userEntity.state = UserState.CITY_PROVIDED
        }
    }

    fun setSearchOptions(externalUserId: String, searchOptions: UserSearchOptionsDto) {
        transaction {
            val userEntity = Users.getByExternalId(externalUserId)

            if (!userEntity.state.canSetSearchOptions()) {
                throw IllegalStateException("Please, provide city first")
            }

            UserSearchOptions.upsert(
                userExternalId = userEntity.externalId,
                searchOptions = searchOptions,
                userCityName = userEntity.location!!.name
            )
            userEntity.state = UserState.SEARCH_OPTIONS_PROVIDED
        }
    }

    fun getSearchOptions(externalId: String): UserSearchOptionsInfo = transaction {
        val user = getUserByExternalId(externalId)

        if (!user.state.canSetSearchOptions()) {
            throw IllegalStateException("Please, provide city")
        }

        UserSearchOptions.getByExternalId(externalId)?.toDomain()
            ?: UserSearchOptionsInfo(
                // we know that user have already provided city
                cityName = getUserByExternalId(externalId).city!!.name
            )
    }

    fun getUserByExternalId(externalId: String) = Users.getByExternalId(externalId).toDomain()
}
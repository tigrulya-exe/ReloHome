package exe.tigrulya.relohome.handler.service

import exe.tigrulya.relohome.handler.repository.Cities
import exe.tigrulya.relohome.handler.repository.UserSearchOptions
import exe.tigrulya.relohome.handler.repository.Users
import exe.tigrulya.relohome.model.*
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.transaction

class UserService {
    fun registerUser(user: UserCreateDto) {
        Users.insertIgnore {
            it[name] = user.name
            it[externalId] = externalId
            it[state] = UserState.NEW
        }
    }

    fun setLocation(externalId: String, city: City) {
        transaction {
            val userEntity = Users.getByExternalId(externalId)

            userEntity.location = Cities.getByName(city.name)
            userEntity.state = UserState.CITY_PROVIDED
        }
    }

    // todo tmp, refactor when proper subscription will be added
    fun enableSubscription(externalId: String) {
        transaction {
            val userEntity = Users.getByExternalId(externalId)
            userEntity.state = UserState.SUBSCRIPTION_PURCHASED
        }
    }

    fun toggleSearch(externalId: String): Boolean = transaction {
        val userEntity = Users.getByExternalId(externalId)
        if (!userEntity.state.searchOptionsProvided()) {
            throw IllegalStateException("Please, provide search parameters first")
        }

        val searchOptions = UserSearchOptions.getByExternalId(externalId)
            ?: throw IllegalStateException("This should never happen, please contact support.")

        !searchOptions.enabled.also {
            searchOptions.enabled = it
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
package exe.tigrulya.relohome.handler.service

import exe.tigrulya.relohome.error.ReloHomeServerException
import exe.tigrulya.relohome.error.ReloHomeUserException
import exe.tigrulya.relohome.handler.repository.*
import exe.tigrulya.relohome.model.*
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.transaction

class UserService {
    fun registerUser(user: UserCreateDto) = transaction {
        Users.insertIgnore {
            it[name] = user.name
            it[externalId] = user.externalId
            it[state] = UserState.NEW
        }
    }

    fun setLocation(externalId: String, city: City) = transaction {
        val userEntity = Users.getByExternalId(externalId)

        userEntity.location = Cities.getByName(city.name)
        userEntity.state = UserState.CITY_PROVIDED
    }

    fun setLocale(externalId: String, locale: String) = transaction {
        val userEntity = Users.getByExternalId(externalId)

        if (!Locales.exists(locale)) {
            throw ReloHomeUserException("Wrong locale: $id")
        }

        userEntity.locale = locale
    }

    // todo tmp, refactor when proper subscription will be added
    fun enableSubscription(externalId: String) = transaction {
        val userEntity = Users.getByExternalId(externalId)
        userEntity.state = UserState.SUBSCRIPTION_PURCHASED
    }

    fun toggleSearch(externalId: String): Boolean = transaction {
        val userEntity = Users.getByExternalId(externalId)
        if (!userEntity.state.searchOptionsProvided()) {
            throw ReloHomeUserException("Please, provide search parameters first")
        }

        val searchOptions = SearchOptions.getByExternalId(externalId)
            ?: throw ReloHomeServerException(
                "Illegal state: user is created, but search options not found. " +
                        "This should never happen."
            )

        with(searchOptions) {
            enabled = !enabled
            enabled
        }
    }

    fun setSearchOptions(externalUserId: String, searchOptions: UserSearchOptionsDto) = transaction {
        val userEntity = Users.getByExternalId(externalUserId)

        if (!userEntity.state.canSetSearchOptions()) {
            throw ReloHomeUserException("Please, provide city first")
        }

        SearchOptions.upsert(
            userExternalId = userEntity.externalId,
            searchOptions = searchOptions,
            userCityName = userEntity.location!!.name
        )
        userEntity.state = UserState.SEARCH_OPTIONS_PROVIDED
    }

    fun getSearchOptions(externalId: String): UserSearchOptionsInfo = transaction {
        val user = getUserByExternalId(externalId)

        if (!user.state.canSetSearchOptions()) {
            throw ReloHomeUserException("Please, provide city")
        }

        SearchOptions.getByExternalId(externalId)?.toDomain()
            ?: UserSearchOptionsInfo(
                // we know that user have already provided city
                cityName = getUserByExternalId(externalId).city!!.name
            )
    }

    private fun getUserByExternalId(externalId: String) = Users.getByExternalId(externalId).toDomain()
}
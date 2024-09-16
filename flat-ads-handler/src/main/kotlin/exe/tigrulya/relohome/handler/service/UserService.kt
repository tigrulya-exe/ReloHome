package exe.tigrulya.relohome.handler.service

import exe.tigrulya.relohome.api.user_handler.UserHandlerGateway
import exe.tigrulya.relohome.error.ReloHomeServerException
import exe.tigrulya.relohome.error.ReloHomeUserException
import exe.tigrulya.relohome.handler.repository.Cities
import exe.tigrulya.relohome.handler.repository.Locales
import exe.tigrulya.relohome.handler.repository.SearchOptions
import exe.tigrulya.relohome.handler.repository.Users
import exe.tigrulya.relohome.model.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class UserService : UserHandlerGateway {
    override suspend fun registerUser(user: UserCreateDto): Unit = newSuspendedTransaction(Dispatchers.IO) {
        Users.insertIgnore {
            it[name] = user.name
            it[externalId] = user.externalId
            it[locale] = user.locale
            it[state] = UserState.NEW
        }
    }

    override suspend fun setLocation(externalId: String, city: City) = newSuspendedTransaction(Dispatchers.IO) {
        val userEntity = Users.getByExternalId(externalId)

        userEntity.location = Cities.getByName(city.name)
        userEntity.state = UserState.CITY_PROVIDED
    }

    override suspend fun setLocale(externalId: String, locale: String) = newSuspendedTransaction(Dispatchers.IO) {
        val userEntity = Users.getByExternalId(externalId)

        if (!Locales.exists(locale)) {
            throw ReloHomeUserException("Wrong locale: $locale")
        }

        userEntity.locale = locale
    }

    // todo tmp, refactor when proper subscription will be added
    suspend fun enableSubscription(externalId: String) = newSuspendedTransaction(Dispatchers.IO) {
        val userEntity = Users.getByExternalId(externalId)
        userEntity.state = UserState.SUBSCRIPTION_PURCHASED
    }

    override suspend fun toggleSearch(externalId: String): Boolean =
        newSuspendedTransaction(Dispatchers.IO) {
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

    // todo move search enabled to Users table
    override suspend fun getUserInfo(externalId: String): UserInfo = newSuspendedTransaction(Dispatchers.IO) {
        Users
            .join(SearchOptions, JoinType.INNER, onColumn = Users.externalId, otherColumn = SearchOptions.externalId)
            .slice(Users.externalId, Users.name, Users.locale, SearchOptions.enabled)
            .select(Users.externalId eq externalId)
            .single()
            .run {
                UserInfo(
                    id = this[Users.externalId],
                    name = this[Users.name],
                    locale = this[Users.locale],
                    searchEnabled = this[SearchOptions.enabled]
                )
            }
    }

    override suspend fun setSearchOptions(externalUserId: String, searchOptions: UserSearchOptionsDto) =
        newSuspendedTransaction(Dispatchers.IO) {
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
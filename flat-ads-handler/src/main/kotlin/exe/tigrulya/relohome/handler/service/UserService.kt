package exe.tigrulya.relohome.handler.service

import exe.tigrulya.relohome.handler.repository.*
import exe.tigrulya.relohome.model.*
import org.jetbrains.exposed.sql.*
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

            UserSearchOptions.upsert(keys = arrayOf(UserSearchOptions.externalId)) {
                it[externalId] = userEntity.externalId
                searchOptions.priceRange.apply {
                    it[priceFrom] = from
                    it[priceTo] = to
                }
                searchOptions.roomRange.apply {
                    it[roomsFrom] = from
                    it[roomsTo] = to
                }
                // we know that we already set the location of user because of his state
                it[cityName] = userEntity.location?.name!!
                it[subDistricts] = searchOptions.subDistricts.let {
                    if (it.isEmpty()) null else it.joinToString(",")
                }
            }

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

    fun getUsersFrom(city: City): List<User> {
        val rows = Users.innerJoin(Cities)
            .slice(Users.columns)
            .select { Users.state eq UserState.SEARCH_OPTIONS_PROVIDED and (Cities.name eq city.name) }
            .withDistinct()

        return UserEntity.wrapRows(rows).map { it.toDomain() }.toList()
    }
}
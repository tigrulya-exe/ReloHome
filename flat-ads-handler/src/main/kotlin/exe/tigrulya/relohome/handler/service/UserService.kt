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

            // todo replace with update
            // UserSearchOptions.update {

            UserSearchOptionsEntity.new {
                externalId = userEntity.externalId
                searchOptions.priceRange.apply {
                    priceFrom = from
                    priceTo = to
                }
                searchOptions.roomRange.apply {
                    roomsFrom = from
                    roomsTo = to
                }
                // we know that we already set the location of user because of his state
                cityName = userEntity.location?.name!!
                subDistricts = searchOptions.subDistricts.let {
                    if (it.isEmpty()) null else it.joinToString(",")
                }
            }
            userEntity.state = UserState.SEARCH_OPTIONS_PROVIDED
        }
    }

    fun getUsersFrom(city: City): List<User> {
        val rows = Users.innerJoin(Cities)
            .slice(Users.columns)
            .select { Users.state eq UserState.SEARCH_OPTIONS_PROVIDED and (Cities.name eq city.name) }
            .withDistinct()

        return UserEntity.wrapRows(rows).map { it.toDomain() }.toList()
    }
}
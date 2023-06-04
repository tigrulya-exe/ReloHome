package exe.tigrulya.relohome.handler.service

import exe.tigrulya.relohome.connector.model.City
import exe.tigrulya.relohome.handler.model.User
import exe.tigrulya.relohome.handler.model.UserCreateDto
import exe.tigrulya.relohome.handler.model.UserState
import exe.tigrulya.relohome.handler.repository.Cities
import exe.tigrulya.relohome.handler.repository.CityEntity
import exe.tigrulya.relohome.handler.repository.UserEntity
import exe.tigrulya.relohome.handler.repository.Users
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class UserService {
    fun registerUser(user: UserCreateDto) {
        UserEntity.new(user.id) {
            name = user.name
            state = UserState.NEW
        }
    }

    fun setLocation(userId: Long, city: City) {
        transaction {
            val userEntity = UserEntity.findById(userId)
                ?: throw IllegalArgumentException("Wrong user id")

            val cityEntity = CityEntity.find { Cities.name eq city.name }
                .firstOrNull()
                ?: throw IllegalArgumentException("Wrong city")

            userEntity.location = cityEntity

            //TODO replace it to following when html page with options will be added:
            // userEntity.state = UserState.CITY_PROVIDED
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
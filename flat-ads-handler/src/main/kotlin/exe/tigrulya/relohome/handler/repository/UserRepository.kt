package exe.tigrulya.relohome.handler.repository

import exe.tigrulya.relohome.handler.model.User
import exe.tigrulya.relohome.handler.model.UserState
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Users : LongIdTable() {
    var name = varchar("name", 50)
    var location = reference("cityId", Cities).nullable()
    var state = enumerationByName<UserState>("state", 10)
}

class UserEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserEntity>(Users)

    var name by Users.name

    var state by Users.state

    var location by CityEntity optionalReferencedOn Users.location

    fun toDomain() = User(
        id = id.value,
        name = name,
        state = state
    )
}

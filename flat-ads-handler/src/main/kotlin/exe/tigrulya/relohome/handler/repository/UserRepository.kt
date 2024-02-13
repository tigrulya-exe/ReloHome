package exe.tigrulya.relohome.handler.repository

import exe.tigrulya.relohome.error.ReloHomeClientException
import exe.tigrulya.relohome.model.User
import exe.tigrulya.relohome.model.UserState
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Users : LongIdTable() {
    var name = varchar("name", 128)
    var externalId = varchar("external_id", 128).uniqueIndex()
    var location = reference("city_id", Cities).nullable()
    var state = enumerationByName<UserState>("state", 64)

    fun getByExternalId(externalId: String) = UserEntity.find {
        Users.externalId eq externalId
    }.firstOrNull() ?: throw ReloHomeClientException("Wrong user id")
}

class UserEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserEntity>(Users)

    var name by Users.name

    var externalId by Users.externalId

    var state by Users.state

    var location by CityEntity optionalReferencedOn Users.location

    fun toDomain() = User(
        id = id.value,
        name = name,
        externalId = externalId,
        state = state,
        city = location?.toDomain()
    )
}

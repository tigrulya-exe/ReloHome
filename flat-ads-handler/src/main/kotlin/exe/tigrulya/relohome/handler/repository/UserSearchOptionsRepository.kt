package exe.tigrulya.relohome.handler.repository

import exe.tigrulya.relohome.model.NumRange
import exe.tigrulya.relohome.model.UserSearchOptionsInfo
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object UserSearchOptions : LongIdTable() {
    // for fast search
    var externalId = varchar("external_id", 128).uniqueIndex()

    // for fast search
    var cityName = varchar("city_name", 50)
    var priceFrom = integer("price_from").nullable()
    var priceTo = integer("price_to").nullable()
    var roomsFrom = integer("rooms_from").nullable()
    var roomsTo = integer("rooms_to").nullable()

    // for fast search
    var subDistricts = varchar("sub_districts", 1024).nullable()

    fun getByExternalId(externalId: String) = UserSearchOptionsEntity.find {
        UserSearchOptions.externalId eq externalId
    }.firstOrNull()
}

class UserSearchOptionsEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserSearchOptionsEntity>(UserSearchOptions)

    var externalId by UserSearchOptions.externalId

    var priceFrom by UserSearchOptions.priceFrom

    var priceTo by UserSearchOptions.priceTo

    var roomsFrom by UserSearchOptions.roomsFrom

    var roomsTo by UserSearchOptions.roomsTo

    var cityName by UserSearchOptions.cityName

    var subDistricts by UserSearchOptions.subDistricts

    fun toDomain() = UserSearchOptionsInfo(
        priceRange = NumRange(priceFrom, priceTo),
        roomRange = NumRange(roomsFrom, roomsTo),
        cityName = cityName,
        subDistricts = subDistricts?.split(",")?.toSet() ?: emptySet()
    )
}
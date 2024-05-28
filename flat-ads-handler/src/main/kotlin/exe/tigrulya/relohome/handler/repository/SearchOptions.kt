package exe.tigrulya.relohome.handler.repository

import exe.tigrulya.relohome.model.NumRange
import exe.tigrulya.relohome.model.UserSearchOptionsDto
import exe.tigrulya.relohome.model.UserSearchOptionsInfo
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.upsert

object SearchOptions : LongIdTable() {
    // for fast search
    var externalId = varchar("external_id", 128).uniqueIndex()

    var enabled = bool("enabled")

    // for fast search
    var cityName = varchar("city_name", 255)
    var priceFrom = integer("price_from").nullable()
    var priceTo = integer("price_to").nullable()
    var roomsFrom = integer("rooms_from").nullable()
    var roomsTo = integer("rooms_to").nullable()
    var areaFrom = integer("area_from").nullable()
    var areaTo = integer("area_to").nullable()

    // for fast search
    var subDistricts = varchar("sub_districts", 4096).nullable()

    fun getByExternalId(externalId: String) = UserSearchOptionsEntity.find {
        SearchOptions.externalId eq externalId
    }.firstOrNull()

    fun upsert(userExternalId: String, searchOptions: UserSearchOptionsDto, userCityName: String) {
        SearchOptions.upsert(keys = arrayOf(externalId)) { entity ->
            entity[externalId] = userExternalId
            entity[enabled] = searchOptions.enabled
            searchOptions.priceRange.apply {
                entity[priceFrom] = from
                entity[priceTo] = to
            }
            searchOptions.roomRange.apply {
                entity[roomsFrom] = from
                entity[roomsTo] = to
            }
            searchOptions.areaRange.apply {
                entity[areaFrom] = from
                entity[areaTo] = to
            }
            // we know that we already set the location of user because of his state
            entity[cityName] = userCityName
            entity[subDistricts] = searchOptions.subDistricts.let {
                if (it.isEmpty()) null else it.joinToString(",")
            }
        }
    }
}

class UserSearchOptionsEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserSearchOptionsEntity>(SearchOptions)

    var externalId by SearchOptions.externalId

    var enabled by SearchOptions.enabled

    var priceFrom by SearchOptions.priceFrom

    var priceTo by SearchOptions.priceTo

    var roomsFrom by SearchOptions.roomsFrom

    var roomsTo by SearchOptions.roomsTo

    var areaFrom by SearchOptions.areaFrom

    var areaTo by SearchOptions.areaTo

    var cityName by SearchOptions.cityName

    var subDistricts by SearchOptions.subDistricts

    fun toDomain() = UserSearchOptionsInfo(
        priceRange = NumRange(priceFrom, priceTo),
        roomRange = NumRange(roomsFrom, roomsTo),
        areaRange = NumRange(areaFrom, areaTo),
        cityName = cityName,
        subDistricts = subDistricts?.split(",")?.toSet() ?: emptySet()
    )
}
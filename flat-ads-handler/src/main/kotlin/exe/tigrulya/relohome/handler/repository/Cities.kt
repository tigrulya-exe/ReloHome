package exe.tigrulya.relohome.handler.repository

import exe.tigrulya.relohome.model.City
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Cities : LongIdTable() {
    val name = varchar("name", 255)
    val country = reference("country_id", Countries)

    fun getByName(name: String) = CityEntity.find { Cities.name eq name }
        .firstOrNull()
        ?: throw IllegalArgumentException("Wrong city")
}

class CityEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<CityEntity>(Cities)

    var name by Cities.name

    var country by CountryEntity referencedOn Cities.country

    fun toDomain() = City(
        name = name,
        country = country.name
    )
}

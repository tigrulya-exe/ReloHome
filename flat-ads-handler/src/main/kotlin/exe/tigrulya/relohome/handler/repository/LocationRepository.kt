package exe.tigrulya.relohome.handler.repository

import exe.tigrulya.relohome.fetcher.model.City
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Countries : LongIdTable() {
    val name = varchar("name", 50)
}

object Cities : LongIdTable() {
    val name = varchar("name", 50)
    val country = reference("countryId", Countries)
}

class CountryEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<CountryEntity>(Countries)

    var name by Countries.name
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
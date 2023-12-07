package exe.tigrulya.relohome.handler.repository

import exe.tigrulya.relohome.model.City
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.select

object Countries : LongIdTable() {
    val name = varchar("name", 50)
}

object Cities : LongIdTable() {
    val name = varchar("name", 50)
    val country = reference("country_id", Countries)

    fun getByName(name: String) = CityEntity.find { Cities.name eq name }
        .firstOrNull()
        ?: throw IllegalArgumentException("Wrong city")
}

object SubDistricts : LongIdTable() {
    val name = varchar("name", 50)
    val city = reference("city_id", Cities)

    // primitive cache
    private val districtsCache = LazyMap<String, List<String>> { cityName ->
        SubDistricts.innerJoin(Cities)
            .slice(name)
            .select { Cities.name eq cityName }
            .map { it[name] }
    }

    fun getByCityName(cityName: String): List<String> = districtsCache[cityName] ?: emptyList()
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

class SubDistrictEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<SubDistrictEntity>(SubDistricts)

    var name by SubDistricts.name

    var city by CityEntity referencedOn SubDistricts.city

    fun toDomain() = name
}
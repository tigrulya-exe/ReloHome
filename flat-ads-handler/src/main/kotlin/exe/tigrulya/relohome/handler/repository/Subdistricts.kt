package exe.tigrulya.relohome.handler.repository

import exe.tigrulya.relohome.util.LazyMap
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.select

object SubDistricts : LongIdTable() {
    val name = varchar("name", 255)
    val city = reference("city_id", Cities)

    // primitive cache
    private val districtsCache = LazyMap<String, List<String>> { cityName ->
        innerJoin(Cities)
            .slice(name)
            .select { Cities.name eq cityName }
            .map { it[name] }
    }

    fun getByCityName(cityName: String): List<String> = districtsCache[cityName] ?: emptyList()
}

class SubDistrictEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<SubDistrictEntity>(SubDistricts)

    var name by SubDistricts.name

    var city by CityEntity referencedOn SubDistricts.city

    fun toDomain() = name
}
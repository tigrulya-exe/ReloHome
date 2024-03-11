package exe.tigrulya.relohome.handler.repository

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Countries : LongIdTable() {
    val name = varchar("name", 255)
}

class CountryEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<CountryEntity>(Countries)

    var name by Countries.name
}
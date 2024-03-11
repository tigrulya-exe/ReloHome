package exe.tigrulya.relohome.handler.repository

import exe.tigrulya.relohome.model.Locale
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

const val DEFAULT_LOCALE = "en"

object Locales : LongIdTable() {
    val strId = varchar("str_id", 32)
    val name = varchar("name", 255)

    fun exists(id: String): Boolean {
        return !LocaleEntity.find { strId eq id }
            .empty()
    }
}

class LocaleEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<LocaleEntity>(Locales)

    var strId by Locales.strId

    var name by Locales.name
    fun toDomain() = Locale(strId, name)
}
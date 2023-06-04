package exe.tigrulya.relohome.handler

import exe.tigrulya.relohome.handler.model.UserState
import exe.tigrulya.relohome.handler.repository.Cities
import exe.tigrulya.relohome.handler.repository.CityEntity
import exe.tigrulya.relohome.handler.repository.Countries
import exe.tigrulya.relohome.handler.repository.CountryEntity
import exe.tigrulya.relohome.handler.repository.UserEntity
import exe.tigrulya.relohome.handler.repository.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    // use connection pooling via Database.connect(dataSource)
    Database.connect("jdbc:sqlite:D:/IdeaProjects/ReloHome/flat-ads-handler/sqlite.db")

    transaction {
        SchemaUtils.create(Users, Cities, Countries)

        val yerevan = CityEntity.new {
            name = "Yerevan"
            country = CountryEntity.new {
                name = "Armenia"
            }
        }

        val tbilisi = CityEntity.new {
            name = "Tbilisi"
            country = CountryEntity.new {
                name = "Georgia"
            }
        }

        UserEntity.new {
            name = "Ashot"
            state = UserState.SEARCH_OPTIONS_PROVIDED
            location = yerevan
        }

        UserEntity.new {
            name = "Gigo"
            state = UserState.SEARCH_OPTIONS_PROVIDED
            location = tbilisi
        }

        UserEntity.new {
            name = "Vako_unconfirmed"
            state = UserState.CITY_PROVIDED
            location = tbilisi
        }
    }
}
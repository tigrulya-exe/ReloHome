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
    // todo replace with migration tool
    // use connection pooling via Database.connect(dataSource)
    Database.connect("jdbc:sqlite:/Users/tigrulya/IdeaProjects/ReloHome/.dev/sqlite.db")

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
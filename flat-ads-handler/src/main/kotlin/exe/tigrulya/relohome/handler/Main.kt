package exe.tigrulya.relohome.handler

import exe.tigrulya.relohome.handler.db.HikariPooledDataSourceFactory
import exe.tigrulya.relohome.handler.db.migration.MigrationManager
import exe.tigrulya.relohome.handler.repository.*
import exe.tigrulya.relohome.handler.server.FlatAdsHandlerGrpcServer
import exe.tigrulya.relohome.model.UserState
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    StartUtils.runMigrations()
    val server = FlatAdsHandlerGrpcServer(8999)
    server.start()
}

object StartUtils {
    fun runMigrations() {
        val dataSource = HikariPooledDataSourceFactory(
//        "jdbc:sqlite:/Users/tigrulya/IdeaProjects/ReloHome/.dev/sqlite.db"
            "jdbc:sqlite:D:/IdeaProjects/ReloHome/flat-ads-handler/sqlite.db"
        ).create()

        Database.connect(dataSource)
        MigrationManager.newInstance(dataSource).migrate()

        transaction {
            SchemaUtils.create(Users, Cities, Countries, UserSearchOptions)

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
                externalId = "ashot777"
                state = UserState.SEARCH_OPTIONS_PROVIDED
                location = yerevan
            }

            UserEntity.new {
                name = "Tigran"
                externalId = "479226955"
                state = UserState.CITY_PROVIDED
                location = tbilisi
            }

            UserEntity.new {
                name = "Vako"
                externalId = "Vako_unconfirmed"
                state = UserState.CITY_PROVIDED
                location = yerevan
            }
        }
    }
}
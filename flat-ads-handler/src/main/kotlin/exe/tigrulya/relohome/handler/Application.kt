package exe.tigrulya.relohome.handler

import com.github.mustachejava.DefaultMustacheFactory
import exe.tigrulya.relohome.handler.controller.configureRouting
import exe.tigrulya.relohome.handler.db.HikariPooledDataSourceFactory
import exe.tigrulya.relohome.handler.db.migration.MigrationManager
import exe.tigrulya.relohome.handler.repository.*
import exe.tigrulya.relohome.model.UserState
import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.event.*

fun main(args: Array<String>) {
    StartUtils.runMigrations()
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureRouting()
    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("templates")
    }
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
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
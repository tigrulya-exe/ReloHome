package exe.tigrulya.relohome.demo

import exe.tigrulya.relohome.fetcher.ExternalFetcherRunner
import exe.tigrulya.relohome.fetcher.WindowTillNowTimestampProvider
import exe.tigrulya.relohome.handler.db.HikariPooledDataSourceFactory
import exe.tigrulya.relohome.handler.db.migration.MigrationManager
import exe.tigrulya.relohome.handler.gateway.InPlaceFlatAdHandlerGateway
import exe.tigrulya.relohome.handler.repository.*
import exe.tigrulya.relohome.handler.service.FlatAdService
import exe.tigrulya.relohome.handler.service.UserService
import exe.tigrulya.relohome.model.UserState
import exe.tigrulya.relohome.notifier.telegram.ReloHomeBot
import exe.tigrulya.relohome.ssge.SsGeFetcher
import exe.tigrulya.relohome.ssge.SsGeFlatAdMapper
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import java.time.temporal.ChronoUnit

private const val MY_ID: Long = 479226955
private const val BOT_USERNAME = "relo_home_bot"
private const val BOT_TOKEN = "NOPE"

fun main() {
    val dataSource = HikariPooledDataSourceFactory(
//        "jdbc:sqlite:/Users/tigrulya/IdeaProjects/ReloHome/.dev/sqlite.db"
        "jdbc:sqlite:D:/IdeaProjects/ReloHome/flat-ads-handler/sqlite.db"
    ).create()

    Database.connect(dataSource)
    MigrationManager.newInstance(dataSource).migrate()

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
            externalId = "ashot777"
            state = UserState.SEARCH_OPTIONS_PROVIDED
            location = yerevan
        }

        UserEntity.new {
            name = "Tigran"
            externalId = "479226955"
            state = UserState.SEARCH_OPTIONS_PROVIDED
            location = tbilisi
        }

        UserEntity.new {
            name = "Vako"
            externalId = "Vako_unconfirmed"
            state = UserState.CITY_PROVIDED
            location = yerevan
        }
    }

    val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
    val reloHomeBot = ReloHomeBot(
        BOT_TOKEN,
        BOT_USERNAME,
        MY_ID
    )
    botsApi.registerBot(reloHomeBot)

    val userService = UserService()
    val flatAdService = FlatAdService(userService, reloHomeBot)

    runBlocking {
        val runner = ExternalFetcherRunner(
            connector = SsGeFetcher(
                lastHandledAdTimestampProvider = WindowTillNowTimestampProvider(1, ChronoUnit.MINUTES)
            ),
            flatAdMapper = SsGeFlatAdMapper(),
            outCollector = InPlaceFlatAdHandlerGateway(flatAdService)
        )
        runner.run()
    }
}
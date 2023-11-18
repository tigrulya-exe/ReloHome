package exe.tigrulya.relohome.demo

import exe.tigrulya.relohome.api.grpc.GrpcUserHandlerGateway
import exe.tigrulya.relohome.fetcher.ExternalFetcherRunner
import exe.tigrulya.relohome.fetcher.WindowTillNowTimestampProvider
import exe.tigrulya.relohome.handler.StartUtils
import exe.tigrulya.relohome.handler.db.HikariPooledDataSourceFactory
import exe.tigrulya.relohome.handler.db.migration.MigrationManager
import exe.tigrulya.relohome.handler.gateway.InPlaceFlatAdHandlerGateway
import exe.tigrulya.relohome.handler.repository.*
import exe.tigrulya.relohome.handler.server.FlatAdsHandlerGrpcServer
import exe.tigrulya.relohome.handler.service.FlatAdService
import exe.tigrulya.relohome.handler.service.UserService
import exe.tigrulya.relohome.model.UserState
import exe.tigrulya.relohome.notifier.telegram.JsonSearchOptionsDeserializer
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
    StartUtils.runMigrations()

    val server = FlatAdsHandlerGrpcServer(8999)
    server.start()

    val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
    val reloHomeBot = ReloHomeBot(
        BOT_TOKEN,
        BOT_USERNAME,
        MY_ID,
        GrpcUserHandlerGateway("localhost:8999"),
        JsonSearchOptionsDeserializer()
    )
    botsApi.registerBot(reloHomeBot)

    val userService = UserService()
    val flatAdService = FlatAdService(userService, reloHomeBot)

    runBlocking {
        val runner = ExternalFetcherRunner(
            connector = SsGeFetcher(
                lastHandledAdTimestampProvider = WindowTillNowTimestampProvider(250, ChronoUnit.HOURS)
            ),
            flatAdMapper = SsGeFlatAdMapper(),
            outCollector = InPlaceFlatAdHandlerGateway(flatAdService)
        )
        runner.run()
    }
}
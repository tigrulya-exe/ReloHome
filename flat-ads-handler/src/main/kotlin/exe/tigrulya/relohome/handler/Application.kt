package exe.tigrulya.relohome.handler

import com.github.mustachejava.DefaultMustacheFactory
import exe.tigrulya.relohome.api.NoOpNotifierGateway
import exe.tigrulya.relohome.handler.controller.configureRouting
import exe.tigrulya.relohome.handler.db.HikariPooledDataSourceFactory
import exe.tigrulya.relohome.handler.db.migration.MigrationManager
import exe.tigrulya.relohome.handler.service.FlatAdService
import exe.tigrulya.relohome.handler.service.UserService
import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import org.jetbrains.exposed.sql.Database
import org.slf4j.event.Level
import javax.sql.DataSource

fun main(args: Array<String>) {
    StartUtils.runMigrations()
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureRouting(
        userService = UserService(),
        flatAdService = FlatAdService(NoOpNotifierGateway)
    )
    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("templates")
    }
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
}

object StartUtils {
    private const val DEFAULT_DB_URL = "jdbc:postgresql://localhost:65432/ReloHome?user=root&password=toor"

    fun runMigrations(jdbcUrl: String = DEFAULT_DB_URL) {
        val dataSource = connectToDb(jdbcUrl)
        MigrationManager.newInstance(dataSource).migrate()
    }

    fun connectToDb(jdbcUrl: String = DEFAULT_DB_URL): DataSource = HikariPooledDataSourceFactory(jdbcUrl)
        .create()
        .also {
            Database.connect(it)
        }
}
package exe.tigrulya.relohome.handler

import com.github.mustachejava.DefaultMustacheFactory
import exe.tigrulya.relohome.handler.controller.configureRouting
import exe.tigrulya.relohome.handler.db.HikariPooledDataSourceFactory
import exe.tigrulya.relohome.handler.db.migration.MigrationManager
import exe.tigrulya.relohome.handler.kafka.KafkaFlatAdConsumer
import exe.tigrulya.relohome.handler.kafka.KafkaFlatAdProducer
import exe.tigrulya.relohome.handler.server.FlatAdsHandlerGrpcServer
import exe.tigrulya.relohome.handler.service.FlatAdService
import exe.tigrulya.relohome.handler.service.UserService
import exe.tigrulya.relohome.kafka.KafkaConsumerConfig
import exe.tigrulya.relohome.kafka.KafkaProducerConfig
import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import org.jetbrains.exposed.sql.Database
import org.slf4j.event.Level
import javax.sql.DataSource
import kotlin.concurrent.thread
import kotlin.time.Duration.Companion.seconds

fun main(args: Array<String>) = HandlerEntryPoint.start(args)

// todo replace with lightweight DI framework
object ServiceRegistry {
    val userService: UserService = UserService()
    val flatAdService: FlatAdService
    val flatAdConsumer: KafkaFlatAdConsumer

    init {
        // todo parse from application properties
        val kafkaConsumerConfig = KafkaConsumerConfig(
            topics = listOf("flat_handler_ads"),
            group = "flat_handler",
            fetchTimeout = 1.seconds,
            bootstrapServers = "localhost:9094"
        )
        flatAdConsumer = KafkaFlatAdConsumer(kafkaConsumerConfig)

        val kafkaProducerConfig = KafkaProducerConfig(
            topic = "flat_notifier_ads",
            bootstrapServers = "localhost:9094"
        )
        val kafkaProducer = KafkaFlatAdProducer(kafkaProducerConfig)

        flatAdService = FlatAdService(kafkaProducer)
    }
}

fun Application.module() {
    configureRouting(
        userService = ServiceRegistry.userService,
        flatAdService = ServiceRegistry.flatAdService
    )
    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("templates")
    }
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
}

object HandlerEntryPoint {
    fun start(args: Array<String>) {
        // build db schema
        StartUtils.runMigrations()

        // start GRPC server
        // todo mb replace with http server?...
        val server = FlatAdsHandlerGrpcServer(port = 8999, userService = ServiceRegistry.userService)
        server.start()

        // start kafka consumer (async)
        thread {
            ServiceRegistry.flatAdConsumer.handleAds { flatAd ->
                ServiceRegistry.flatAdService.handle(flatAd)
            }
        }

        // start ktor web server (sync)
        io.ktor.server.netty.EngineMain.main(args)
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
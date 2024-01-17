package exe.tigrulya.relohome.handler

import com.github.mustachejava.DefaultMustacheFactory
import exe.tigrulya.relohome.config.Configuration
import exe.tigrulya.relohome.handler.config.ConfigOptions.FLAT_AD_HANDLER_DB_URL
import exe.tigrulya.relohome.handler.config.ConfigOptions.FLAT_AD_HANDLER_GATEWAY_PORT
import exe.tigrulya.relohome.handler.config.ConfigOptions.KAFKA_FLAT_AD_CONSUMER_BOOTSTRAP_SERVERS
import exe.tigrulya.relohome.handler.config.ConfigOptions.KAFKA_FLAT_AD_CONSUMER_GROUP
import exe.tigrulya.relohome.handler.config.ConfigOptions.KAFKA_FLAT_AD_CONSUMER_TOPICS
import exe.tigrulya.relohome.handler.config.ConfigOptions.KAFKA_FLAT_AD_FETCH_TIMEOUT
import exe.tigrulya.relohome.handler.config.ConfigOptions.KAFKA_FLAT_AD_PRODUCER_BOOTSTRAP_SERVERS
import exe.tigrulya.relohome.handler.config.ConfigOptions.KAFKA_FLAT_AD_PRODUCER_TOPIC
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
import exe.tigrulya.relohome.kafka.splitTopics
import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import org.jetbrains.exposed.sql.Database
import org.slf4j.event.Level
import javax.sql.DataSource
import kotlin.concurrent.thread

fun main(args: Array<String>) = HandlerEntryPoint.start(args)

// todo replace with lightweight DI framework
object ServiceRegistry {
    val userService: UserService = UserService()
    val flatAdService: FlatAdService
    val flatAdConsumer: KafkaFlatAdConsumer
    val config: Configuration = Configuration.fromResource("handler.yaml")

    init {
        // todo parse from application properties
        val kafkaConsumerConfig = KafkaConsumerConfig(
            bootstrapServers = config.get(KAFKA_FLAT_AD_CONSUMER_BOOTSTRAP_SERVERS),
            topics = splitTopics(config.get(KAFKA_FLAT_AD_CONSUMER_TOPICS)),
            group = config.get(KAFKA_FLAT_AD_CONSUMER_GROUP),
            fetchTimeout = config.get(KAFKA_FLAT_AD_FETCH_TIMEOUT)
        )
        flatAdConsumer = KafkaFlatAdConsumer(kafkaConsumerConfig)

        val kafkaProducerConfig = KafkaProducerConfig(
            bootstrapServers = config.get(KAFKA_FLAT_AD_PRODUCER_BOOTSTRAP_SERVERS),
            topic = config.get(KAFKA_FLAT_AD_PRODUCER_TOPIC)
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
        val dbUrl = ServiceRegistry.config.get(FLAT_AD_HANDLER_DB_URL)
        StartUtils.runMigrations(dbUrl)

        // start GRPC server
        // todo mb replace with http server?...
        val grpcPort = ServiceRegistry.config.get(FLAT_AD_HANDLER_GATEWAY_PORT)
        val server = FlatAdsHandlerGrpcServer(port = grpcPort, userService = ServiceRegistry.userService)
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
    fun runMigrations(jdbcUrl: String) {
        val dataSource = connectToDb(jdbcUrl)
        MigrationManager.newInstance(dataSource).migrate()
    }

    fun connectToDb(jdbcUrl: String): DataSource = HikariPooledDataSourceFactory(jdbcUrl)
        .create()
        .also {
            Database.connect(it)
        }
}
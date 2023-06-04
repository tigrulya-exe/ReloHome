package exe.tigrulya.relohome.handler.gateway

import exe.tigrulya.relohome.connector.model.FlatAd
import exe.tigrulya.relohome.handler.service.FlatAdService
import exe.tigrulya.relohome.handler.service.UserService
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

interface FlatAdServiceGateway {
    companion object {
        fun create(): FlatAdServiceGateway = InPlaceFlatAdServiceGateway()
    }

    fun handleFlatAd(flatAd: FlatAd)
}

// TODO tmp implemnent KafkaGateway
class InPlaceFlatAdServiceGateway : FlatAdServiceGateway {
    private val flatAdService: FlatAdService

    init {
        Database.connect("jdbc:sqlite:D:/IdeaProjects/ReloHome/flat-ads-handler/sqlite.db")

        val userService = UserService()
        flatAdService = FlatAdService(userService)
    }

    override fun handleFlatAd(flatAd: FlatAd) {
        transaction {
            addLogger(StdOutSqlLogger)
            flatAdService.handleAd(flatAd)
        }
    }
}
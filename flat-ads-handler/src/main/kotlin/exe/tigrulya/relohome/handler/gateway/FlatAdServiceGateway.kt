package exe.tigrulya.relohome.handler.gateway

import exe.tigrulya.relohome.api.FlatAdHandlerGateway
import exe.tigrulya.relohome.handler.service.FlatAdService
import exe.tigrulya.relohome.handler.service.UserService
import exe.tigrulya.relohome.model.FlatAd
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction


// TODO tmp implemnent KafkaGateway
class InPlaceFlatAdHandlerGateway(private val flatAdService: FlatAdService) : FlatAdHandlerGateway {

    init {
//        Database.connect("jdbc:sqlite:/Users/tigrulya/IdeaProjects/ReloHome/.dev/sqlite.db")
//        Database.connect("jdbc:sqlite:D:/IdeaProjects/ReloHome/flat-ads-handler/sqlite.db")

//        val userService = UserService()
//        flatAdService = FlatAdService(userService)
    }

    override fun handle(flatAd: FlatAd) {
        transaction {
//            addLogger(StdOutSqlLogger)
            flatAdService.handle(flatAd)
        }
    }
}
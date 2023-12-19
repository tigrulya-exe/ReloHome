package exe.tigrulya.relohome.handler.gateway

import exe.tigrulya.relohome.api.FlatAdHandlerGateway
import exe.tigrulya.relohome.handler.service.FlatAdService
import exe.tigrulya.relohome.model.FlatAd
import org.jetbrains.exposed.sql.transactions.transaction


// TODO tmp implemnent KafkaGateway
class InPlaceFlatAdHandlerGateway(private val flatAdService: FlatAdService) : FlatAdHandlerGateway {

    override suspend fun handle(flatAd: FlatAd) {
        transaction {
//            addLogger(StdOutSqlLogger)
            flatAdService.handle(flatAd)
        }
    }
}
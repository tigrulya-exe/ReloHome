package exe.tigrulya.relohome.handler.gateway

import exe.tigrulya.relohome.api.FlatAdHandlerGateway
import exe.tigrulya.relohome.handler.service.FlatAdService
import exe.tigrulya.relohome.model.FlatAd


class InPlaceFlatAdHandlerGateway(private val flatAdService: FlatAdService) : FlatAdHandlerGateway {

    override suspend fun handle(flatAd: FlatAd) {
        flatAdService.handle(flatAd)
    }
}
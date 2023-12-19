package exe.tigrulya.relohome.api

import exe.tigrulya.relohome.model.FlatAd

interface FlatAdHandlerGateway {
    suspend fun handle(flatAd: FlatAd)
}
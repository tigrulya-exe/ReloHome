package exe.tigrulya.relohome.api

import exe.tigrulya.relohome.model.FlatAd

interface FlatAdHandlerGateway {
    fun handle(flatAd: FlatAd)
}
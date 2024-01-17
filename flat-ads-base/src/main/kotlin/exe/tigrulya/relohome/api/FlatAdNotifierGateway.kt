package exe.tigrulya.relohome.api

import exe.tigrulya.relohome.model.FlatAd

interface FlatAdNotifierGateway {
    suspend fun onNewAd(userIds: List<String>, flatAd: FlatAd)
}

object NoOpNotifierGateway: FlatAdNotifierGateway {
    override suspend fun onNewAd(userIds: List<String>, flatAd: FlatAd) {}
}
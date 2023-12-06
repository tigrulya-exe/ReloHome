package exe.tigrulya.relohome.api

import exe.tigrulya.relohome.model.FlatAd

interface FlatAdNotifierGateway {
    fun onNewAd(userId: String, flatAd: FlatAd)
}

object NoOpNotifierGateway: FlatAdNotifierGateway {
    override fun onNewAd(userId: String, flatAd: FlatAd) {}
}
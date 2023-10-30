package exe.tigrulya.relohome.api

import exe.tigrulya.relohome.model.FlatAd
import exe.tigrulya.relohome.model.User

interface FlatAdNotifierGateway {
    fun onNewAd(user: User, flatAd: FlatAd)
}
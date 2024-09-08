package exe.tigrulya.relohome.api

import exe.tigrulya.relohome.model.FlatAd
import exe.tigrulya.relohome.model.UserInfo

interface FlatAdNotifierGateway {
    suspend fun onNewAd(users: List<UserInfo>, flatAd: FlatAd)

}
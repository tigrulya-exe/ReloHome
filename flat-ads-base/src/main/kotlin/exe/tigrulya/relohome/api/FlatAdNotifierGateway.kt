package exe.tigrulya.relohome.api

import exe.tigrulya.relohome.model.FlatAd

interface FlatAdNotifierGateway {
    suspend fun onNewAd(userIds: List<String>, flatAd: FlatAd)

    // todo
//    suspend fun onNewAd(users: List<UserInfo>, flatAd: FlatAd)

}
package exe.tigrulya.relohome.handler.service

import exe.tigrulya.relohome.api.FlatAdHandlerGateway
import exe.tigrulya.relohome.api.FlatAdNotifierGateway
import exe.tigrulya.relohome.model.FlatAd

class FlatAdService(
    private val userService: UserService,
    private val notifierGateway: FlatAdNotifierGateway
) : FlatAdHandlerGateway {
    override fun handle(flatAd: FlatAd) {
        userService.getUsersFrom(flatAd.address.city)
            .forEach { notifierGateway.onNewAd(it, flatAd) }
    }
}
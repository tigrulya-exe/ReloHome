package exe.tigrulya.relohome.handler.service

import exe.tigrulya.relohome.connector.model.FlatAd

class FlatAdService(
    private val userService: UserService
) {
    fun handleAd(flatAd: FlatAd) {
        val userNames = userService.getUsersFrom(flatAd.address.city)
            .map { it.name }

        println("Send $flatAd to $userNames")
    }
}
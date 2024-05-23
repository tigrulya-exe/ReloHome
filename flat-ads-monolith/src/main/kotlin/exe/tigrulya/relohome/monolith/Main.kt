package exe.tigrulya.relohome.monolith

import exe.tigrulya.relohome.api.FlatAdNotifierGateway
import exe.tigrulya.relohome.api.user_handler.async
import exe.tigrulya.relohome.handler.HandlerEntryPoint
import exe.tigrulya.relohome.handler.ServiceRegistry
import exe.tigrulya.relohome.handler.service.FlatAdService
import exe.tigrulya.relohome.model.FlatAd
import exe.tigrulya.relohome.notifier.telegram.TgNotifierEntryPoint
import exe.tigrulya.relohome.ssge.SsGeFetcherEntryPoint
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread


fun main(args: Array<String>) {
    val flatAdChannel = Channel<Pair<List<String>, FlatAd>>(200)
    val bufferedNotifier = BufferedFlatAdNotifierGateway(flatAdChannel)

    ServiceRegistry.flatAdService = FlatAdService(bufferedNotifier)

    HandlerEntryPoint.startInPlace(args)

    thread {
        SsGeFetcherEntryPoint.startInPlace(ServiceRegistry.flatAdService)
    }

    runBlocking {
        val logger = LoggerFactory.getLogger(BufferedFlatAdNotifierGateway::class.java)

        val flatAdNotifier = TgNotifierEntryPoint.startInPlace(ServiceRegistry.userService.async())
        flatAdChannel.consumeEach {
            try {
                flatAdNotifier.onNewAd(it.first, it.second)
            } catch (exception: Exception) {
                logger.error("Error reporting flat ad ${it.second} to users ${it.first} through notifier", exception)
            }
        }
    }
}

class BufferedFlatAdNotifierGateway(
    private val buffer: SendChannel<Pair<List<String>, FlatAd>>
) : FlatAdNotifierGateway {

    override suspend fun onNewAd(userIds: List<String>, flatAd: FlatAd) {
        buffer.send(userIds to flatAd)
    }
}
package exe.tigrulya.relohome.demo

import exe.tigrulya.relohome.handler.HandlerEntryPoint
import exe.tigrulya.relohome.notifier.telegram.TgNotifierEntryPoint
import exe.tigrulya.relohome.ssge.SsGeFetcherEntryPoint
import kotlin.concurrent.thread


fun main(args: Array<String>) {

    thread(name = "FlatAdsHandlerMain") {
        HandlerEntryPoint.start(args)
    }

    thread(name = "TgNotifierMain") {
        TgNotifierEntryPoint.start()
    }

    SsGeFetcherEntryPoint.start()
}
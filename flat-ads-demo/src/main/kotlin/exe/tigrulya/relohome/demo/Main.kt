package exe.tigrulya.relohome.demo

import exe.tigrulya.relohome.fetcher.ExternalFetcherRunner
import exe.tigrulya.relohome.fetcher.KafkaFlatAdProducer
import exe.tigrulya.relohome.fetcher.WindowTillNowTimestampProvider
import exe.tigrulya.relohome.handler.HandlerEntryPoint
import exe.tigrulya.relohome.kafka.KafkaProducerConfig
import exe.tigrulya.relohome.notifier.telegram.NotifierEntryPoint
import exe.tigrulya.relohome.ssge.SsGeFetcher
import kotlinx.coroutines.runBlocking
import java.time.temporal.ChronoUnit
import kotlin.concurrent.thread


fun main(args: Array<String>) {

    thread {
        HandlerEntryPoint.start(args)
    }

    thread {
        NotifierEntryPoint.start(args)
    }

    runBlocking {
        val runner = ExternalFetcherRunner(
            fetcher = SsGeFetcher(
                lastHandledAdTimestampProvider = WindowTillNowTimestampProvider(10, ChronoUnit.MINUTES)
            ),
            outCollector = KafkaFlatAdProducer(
                KafkaProducerConfig(
                    topic = "flat_handler_ads",
                    bootstrapServers = "localhost:9094"
                )
            )
        )
        runner.run()
    }
}
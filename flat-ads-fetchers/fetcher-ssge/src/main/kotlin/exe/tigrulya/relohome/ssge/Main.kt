package exe.tigrulya.relohome.ssge

import exe.tigrulya.relohome.config.Configuration
import exe.tigrulya.relohome.fetcher.ExternalFetcherRunner
import exe.tigrulya.relohome.fetcher.FileBackedLastHandledAdTimestampProvider
import exe.tigrulya.relohome.fetcher.KafkaFlatAdProducer
import exe.tigrulya.relohome.fetcher.WindowTillNowTimestampProvider
import exe.tigrulya.relohome.fetcher.config.ConfigOptions.FLAT_AD_FETCHER_LAST_HANDLED_TIMESTAMP_PATH
import exe.tigrulya.relohome.fetcher.config.ConfigOptions.FLAT_AD_FETCHER_LAST_HANDLED_TIMESTAMP_WINDOW
import exe.tigrulya.relohome.kafka.KafkaProducerConfig
import exe.tigrulya.relohome.ssge.config.ConfigOptions.FLAT_AD_FETCHER_SS_GE_ASYNC_BUFFER_CAPACITY
import exe.tigrulya.relohome.ssge.config.ConfigOptions.FLAT_AD_FETCHER_SS_GE_BASE_URL
import exe.tigrulya.relohome.ssge.config.ConfigOptions.KAFKA_FLAT_AD_PRODUCER_BOOTSTRAP_SERVERS
import exe.tigrulya.relohome.ssge.config.ConfigOptions.KAFKA_FLAT_AD_PRODUCER_TOPIC
import kotlinx.coroutines.runBlocking
import java.nio.file.Path

fun main() = SsGeFetcherEntryPoint.start()

object SsGeFetcherEntryPoint {
    fun start() = runBlocking {
        val config: Configuration = Configuration.fromResource("fetcher-ssge.yaml")

        val lastHandledAdTimestampProvider = FileBackedLastHandledAdTimestampProvider(
            snapshotFilePath = Path.of(
                config.get(FLAT_AD_FETCHER_LAST_HANDLED_TIMESTAMP_PATH)
            ),
            delegateTimestampProvider = WindowTillNowTimestampProvider(
                config.getDuration(FLAT_AD_FETCHER_LAST_HANDLED_TIMESTAMP_WINDOW)
            )
        )

        val runner = ExternalFetcherRunner(
            fetcher = SsGeFetcher(
                baseUrl = config.get(FLAT_AD_FETCHER_SS_GE_BASE_URL),
                lastHandledAdTimestampProvider = lastHandledAdTimestampProvider,
                asyncBufferCapacity = config.get(FLAT_AD_FETCHER_SS_GE_ASYNC_BUFFER_CAPACITY)
            ),
            outCollector = KafkaFlatAdProducer(
                KafkaProducerConfig(
                    bootstrapServers = config.get(KAFKA_FLAT_AD_PRODUCER_BOOTSTRAP_SERVERS),
                    topic = config.get(KAFKA_FLAT_AD_PRODUCER_TOPIC)
                )
            )
        )

        runner.run()
    }
}
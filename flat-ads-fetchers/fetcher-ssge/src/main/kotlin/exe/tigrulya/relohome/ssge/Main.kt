package exe.tigrulya.relohome.ssge

import exe.tigrulya.relohome.api.FlatAdHandlerGateway
import exe.tigrulya.relohome.config.Configuration
import exe.tigrulya.relohome.fetcher.runner.ExternalFetcherRunner
import exe.tigrulya.relohome.fetcher.FileBackedLastHandledAdTimestampProvider
import exe.tigrulya.relohome.fetcher.KafkaFlatAdProducer
import exe.tigrulya.relohome.fetcher.WindowTillNowTimestampProvider
import exe.tigrulya.relohome.fetcher.config.FetcherConfigOptions.FLAT_AD_FETCHER_LAST_HANDLED_TIMESTAMP_PATH
import exe.tigrulya.relohome.fetcher.config.FetcherConfigOptions.FLAT_AD_FETCHER_LAST_HANDLED_TIMESTAMP_WINDOW
import exe.tigrulya.relohome.fetcher.runner.FetcherFactory
import exe.tigrulya.relohome.kafka.KafkaProducerConfig
import exe.tigrulya.relohome.ssge.config.SsGeFetcherConfigOptions.FLAT_AD_FETCHER_SS_GE_ASYNC_BUFFER_CAPACITY
import exe.tigrulya.relohome.ssge.config.SsGeFetcherConfigOptions.FLAT_AD_FETCHER_SS_GE_BASE_URL
import exe.tigrulya.relohome.ssge.config.SsGeFetcherConfigOptions.KAFKA_FLAT_AD_PRODUCER_BOOTSTRAP_SERVERS
import exe.tigrulya.relohome.ssge.config.SsGeFetcherConfigOptions.KAFKA_FLAT_AD_PRODUCER_TOPIC
import exe.tigrulya.relohome.ssge.model.SsGeFlatAdContainer
import java.nio.file.Path

fun main() = SsGeFetcherEntryPoint.startRemote()

object SsGeFetcherEntryPoint {
    fun startRemote() {
        val config: Configuration = Configuration.fromResource("fetcher-ssge.yaml")

        val kafkaFlatAdProducer = KafkaFlatAdProducer(
            KafkaProducerConfig(
                bootstrapServers = config.get(KAFKA_FLAT_AD_PRODUCER_BOOTSTRAP_SERVERS),
                topic = config.get(KAFKA_FLAT_AD_PRODUCER_TOPIC)
            )
        )

        startInternal(config, kafkaFlatAdProducer)
    }

    fun startInPlace(flatAdHandlerGateway: FlatAdHandlerGateway) {
        val config: Configuration = Configuration.fromResource("fetcher-ssge.yaml")
        startInternal(config, flatAdHandlerGateway)
    }

    private fun startInternal(config: Configuration, flatCollector: FlatAdHandlerGateway) {
        val lastHandledAdTimestampProvider = FileBackedLastHandledAdTimestampProvider(
            snapshotFilePath = Path.of(
                config.get(FLAT_AD_FETCHER_LAST_HANDLED_TIMESTAMP_PATH)
            ),
            delegateTimestampProvider = WindowTillNowTimestampProvider(
                config.getDuration(FLAT_AD_FETCHER_LAST_HANDLED_TIMESTAMP_WINDOW)
            )
        )

        val fetcherConstructor: FetcherFactory<SsGeFlatAdContainer> = {
            SsGeFetcher(
                baseUrl = config.get(FLAT_AD_FETCHER_SS_GE_BASE_URL),
                lastHandledAdTimestampProvider = lastHandledAdTimestampProvider,
                asyncBufferCapacity = config.get(FLAT_AD_FETCHER_SS_GE_ASYNC_BUFFER_CAPACITY)
            )
        }

        val runner = ExternalFetcherRunner(
            fetcherFactory = fetcherConstructor,
            outCollector = flatCollector
        )

        runner.run()
    }
}
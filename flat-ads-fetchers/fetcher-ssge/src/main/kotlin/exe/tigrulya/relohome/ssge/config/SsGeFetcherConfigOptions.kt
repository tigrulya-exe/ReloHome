package exe.tigrulya.relohome.ssge.config

import exe.tigrulya.relohome.config.ConfigOption

object SsGeFetcherConfigOptions {

    val KAFKA_FLAT_AD_PRODUCER_TOPIC = ConfigOption(
        name = "kafka.flat-ads.producer.topic",
        defaultValue = "flat_handler_ads"
    )

    val KAFKA_FLAT_AD_PRODUCER_BOOTSTRAP_SERVERS = ConfigOption(
        name = "kafka.flat-ads.producer.bootstrap-servers",
        defaultValue = "localhost:9094"
    )

    val FLAT_AD_FETCHER_SS_GE_BASE_URL = ConfigOption(
        name = "flat-ads.fetcher.ss-ge.url.base",
        defaultValue = "https://api-gateway.ss.ge/v1/"
    )

    val FLAT_AD_FETCHER_SS_GE_ASYNC_BUFFER_CAPACITY = ConfigOption(
        name = "flat-ads.fetcher.ss-ge.buffer.capacity",
        defaultValue = 50
    )
}
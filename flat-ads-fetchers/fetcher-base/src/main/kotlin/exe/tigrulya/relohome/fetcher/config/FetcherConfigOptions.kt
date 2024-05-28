package exe.tigrulya.relohome.fetcher.config

import exe.tigrulya.relohome.config.ConfigOption

object FetcherConfigOptions {

    val FLAT_AD_FETCHER_LAST_HANDLED_TIMESTAMP_PATH = ConfigOption(
        name = "flat-ads.fetcher.timestamp.last-handled.path",
        defaultValue = "last_ad.timestamp"
    )

    val FLAT_AD_FETCHER_LAST_HANDLED_TIMESTAMP_WINDOW = ConfigOption(
        name = "flat-ads.fetcher.timestamp.last-handled.window",
        defaultValue = "12h"
    )

    val FLAT_AD_FETCHER_ASYNC_BUFFER_CAPACITY = ConfigOption(
        name = "flat-ads.fetcher.async.buffer.capacity",
        defaultValue = 50
    )
}
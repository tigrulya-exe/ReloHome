package exe.tigrulya.relohome.fetcher.config

import exe.tigrulya.relohome.config.ConfigOption

object ConfigOptions {

    val FLAT_AD_FETCHER_LAST_HANDLED_TIMESTAMP_PATH = ConfigOption(
        name = "flat-ads.fetcher.timestamp.last-handled.path",
        defaultValue = "last_ad.timestamp"
    )

    val FLAT_AD_FETCHER_LAST_HANDLED_TIMESTAMP_WINDOW = ConfigOption(
        name = "flat-ads.fetcher.timestamp.last-handled.window",
        defaultValue = "12h"
    )
}
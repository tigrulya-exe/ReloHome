package exe.tigrulya.relohome.notifier.telegram.config

import exe.tigrulya.relohome.config.ConfigOption
import kotlin.time.Duration.Companion.seconds

object ConfigOptions {
    val FLAT_AD_HANDLER_GRPC_GATEWAY_HOSTNAME = ConfigOption(
        name = "flat-ads.handler.gateway.grpc.hostname",
        defaultValue = "localhost:8999"
    )

    val FLAT_AD_HANDLER_HTTP_GATEWAY_HOSTNAME = ConfigOption(
        name = "flat-ads.handler.gateway.http.hostname",
        defaultValue = "https://127.0.0.1:8443"
    )

    val FLAT_AD_NOTIFIER_TG_TOKEN = ConfigOption<String>(
        name = "flat-ads.notifier.telegram.token",
    )

    val FLAT_AD_NOTIFIER_TG_BOT_NAME = ConfigOption(
        name = "flat-ads.notifier.telegram.bot.name",
        defaultValue = "relo_home_bot"
    )

    val FLAT_AD_NOTIFIER_TG_CREATOR_ID = ConfigOption(
        name = "flat-ads.notifier.telegram.creator.id",
        defaultValue = 479226955L
    )

    val FLAT_AD_NOTIFIER_TG_REQUESTS_PER_SEC = ConfigOption(
        name = "flat-ads.notifier.telegram.requests.per-second",
        defaultValue = 10
    )
}
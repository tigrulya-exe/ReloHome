package exe.tigrulya.relohome.ssge

import exe.tigrulya.relohome.connector.ExternalFetcherRunner
import exe.tigrulya.relohome.connector.WindowTillNowTimestampProvider
import kotlinx.coroutines.runBlocking
import java.time.temporal.ChronoUnit

fun main(): Unit = runBlocking {
    val runner = ExternalFetcherRunner(
        connector = SsGeFetcher(
            lastHandledAdTimestampProvider = WindowTillNowTimestampProvider(70, ChronoUnit.MINUTES)
        ),
        flatAdMapper = SsGeFlatAdMapper
    )
    runner.run()
}


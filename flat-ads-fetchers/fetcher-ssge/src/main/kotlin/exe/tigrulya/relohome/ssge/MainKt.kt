package exe.tigrulya.relohome.ssge

import exe.tigrulya.relohome.connector.ExternalFetcherRunner
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val runner = ExternalFetcherRunner(
        connector = SsGeFetcher(),
        flatAdMapper = SsGeFlatAdMapper
    )
    runner.run()
}


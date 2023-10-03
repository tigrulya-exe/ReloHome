package exe.tigrulya.relohome.listam

import exe.tigrulya.relohome.connector.ExternalFetcherRunner
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val runner = ExternalFetcherRunner(
        connector = ListAmFetcher(),
        flatAdMapper = ListAmFlatAdMapper
    )
    runner.run()
}


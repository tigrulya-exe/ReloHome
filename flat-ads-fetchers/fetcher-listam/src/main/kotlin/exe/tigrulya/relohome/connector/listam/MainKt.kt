package exe.tigrulya.relohome.connector.listam

import exe.tigrulya.relohome.connector.ExternalConnectorRunner
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val runner = ExternalConnectorRunner(
        connector = ListAmConnector(),
        flatAdMapper = ListAmFlatAdMapper
    )
    runner.run()
}


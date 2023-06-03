package exe.tigrulya.relohome.connector

import exe.tigrulya.relohome.connector.model.FlatAd
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

interface FlatAdMapper<T> {
    fun toFlatAd(externalFlatAd: T): FlatAd
}

interface ExternalConnector<T> {
    fun fetchAds(): Flow<T>
}

abstract class AbstractExternalConnector<T> : ExternalConnector<T> {
    override fun fetchAds(): Flow<T> = flow {
        while (true) {
            fetchBatch(this)
        }
    }

    protected abstract fun fetchBatch(collector: FlowCollector<T>)
}

class ExternalConnectorRunner<T>(
    private val connector: ExternalConnector<T>,
    private val flatAdMapper: FlatAdMapper<T>,
    private val outCollector: FlatAdsCollector
) {
    fun run() = runBlocking {
        connector.fetchAds()
            .map { flatAdMapper.toFlatAd(it) }
            .buffer()
            .map { outCollector.collect(it) }
    }
}
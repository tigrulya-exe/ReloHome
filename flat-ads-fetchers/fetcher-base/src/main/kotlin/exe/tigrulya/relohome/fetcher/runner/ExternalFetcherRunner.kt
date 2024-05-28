package exe.tigrulya.relohome.fetcher.runner

import exe.tigrulya.relohome.api.FlatAdHandlerGateway
import exe.tigrulya.relohome.fetcher.ExternalFetcher
import exe.tigrulya.relohome.util.LoggerProperty
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

typealias FetcherFactory<T> = () -> ExternalFetcher<T>

class ExternalFetcherRunner<T>(
    private val fetcherFactory: FetcherFactory<T>,
    private val outCollector: FlatAdHandlerGateway,
    failoverStrategy: FetcherRunnerFailoverStrategy = FetcherRunnerFailoverStrategy.RECREATE_FETCHER,
    private val asyncBufferCapacity: Int = 50
) {

    private val fetcherRunnerFailover = FetcherRunnerFailover.of(failoverStrategy)
    private val logger by LoggerProperty()
    fun run() = runBlocking {
        var fetcher = fetcherFactory.invoke()
        var flatAdMapper = fetcher.flatAdMapper()

        while (true) {
            try {
                fetcher.fetchAds()
                    .map { flatAdMapper.toFlatAd(it) }
                    .buffer(asyncBufferCapacity)
                    .collect { outCollector.handle(it) }
            } catch (exception: Exception) {
                fetcher = failover(exception, fetcher)
                flatAdMapper = fetcher.flatAdMapper()
            }
        }
    }

    private fun failover(exception: Exception, fetcher: ExternalFetcher<T>): ExternalFetcher<T> {
        logger.error("Error fetching ads: ${exception.message}")
        exception.printStackTrace()
        return fetcherRunnerFailover.onFetcherFail(exception, fetcher, fetcherFactory)
    }
}
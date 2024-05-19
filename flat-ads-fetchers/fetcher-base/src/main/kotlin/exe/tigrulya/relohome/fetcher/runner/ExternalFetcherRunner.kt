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
    failoverStrategy: FetcherFailoverStrategy = FetcherFailoverStrategy.RECREATE_FETCHER
) {

    private val fetcherFailover = FetcherFailover.of(failoverStrategy)
    private val logger by LoggerProperty()
    fun run() = runBlocking {
        var fetcher = fetcherFactory.invoke()
        var flatAdMapper = fetcher.flatAdMapper()

        while (true) {
            try {
                fetcher.fetchAds()
                    .map { flatAdMapper.toFlatAd(it) }
                    .buffer()
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
        return fetcherFailover.failover(exception, fetcher, fetcherFactory)
    }
}
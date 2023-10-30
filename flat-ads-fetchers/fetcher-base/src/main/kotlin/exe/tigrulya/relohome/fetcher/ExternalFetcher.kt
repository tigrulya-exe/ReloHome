package exe.tigrulya.relohome.fetcher

import exe.tigrulya.relohome.api.FlatAdHandlerGateway
import exe.tigrulya.relohome.model.FlatAd
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface FlatAdMapper<T> {
    fun toFlatAd(externalFlatAd: T): FlatAd
}

interface ExternalFetcher<T> {
    fun fetchAds(): Flow<T>
}

abstract class AbstractExternalFetcher<T>(
    protected val lastHandledAdTimestampProvider: LastHandledAdTimestampProvider,
    // todo replace with rate limiter
    private val batchFetchDelay: Duration = 5.seconds,
    private val pageFetchDelay: Duration = 5.seconds
) : ExternalFetcher<T> {

    protected lateinit var lastHandledAdTime: Instant

    override fun fetchAds(): Flow<T> = flow {
        while (true) {
            fetchBatch(this)
            delay(batchFetchDelay)
        }
    }

    private suspend fun fetchBatch(collector: FlowCollector<T>) {
        var pageNum = 1

        // used just for testing
        lastHandledAdTime = lastHandledAdTimestampProvider.provide() ?: Instant.now()
        var lastHandledAdInBatchTime = lastHandledAdTime
        do {
            when (val fetchResult = fetchPage(collector, pageNum)) {
                is FetchResult.NextPageRequired -> ++pageNum
                is FetchResult.Completed -> {
                    lastHandledAdInBatchTime = maxOf(
                        lastHandledAdInBatchTime,
                        fetchResult.lastAdTimestamp
                    )
                    break
                }
            }
            delay(pageFetchDelay)
        } while (true)
        lastHandledAdTimestampProvider.update(lastHandledAdInBatchTime)
    }

    protected abstract suspend fun fetchPage(
        collector: FlowCollector<T>,
        page: Int
    ): FetchResult

    sealed interface FetchResult {
        class Completed(val lastAdTimestamp: Instant) : FetchResult
        object NextPageRequired : FetchResult
    }
}

class ExternalFetcherRunner<T>(
    private val connector: ExternalFetcher<T>,
    private val flatAdMapper: FlatAdMapper<T>,
    private val outCollector: FlatAdHandlerGateway
) {
    fun run() = runBlocking {
        connector.fetchAds()
            .map { flatAdMapper.toFlatAd(it) }
            .buffer()
            .collect { outCollector.handle(it) }
    }
}
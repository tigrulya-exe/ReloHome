package exe.tigrulya.relohome.fetcher

import exe.tigrulya.relohome.model.FlatAd
import exe.tigrulya.relohome.util.LoggerProperty
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface FlatAdMapper<T> {
    fun toFlatAd(externalFlatAd: T): FlatAd
}

interface ExternalFetcher<T> {
    fun fetchAds(): Flow<T>

    fun flatAdMapper(): FlatAdMapper<T>
}

abstract class AbstractExternalFetcher<T>(
    private val lastHandledAdTimestampProvider: LastHandledAdTimestampProvider,
    failoverStrategy: FetcherFailoverStrategy = FetcherFailoverStrategy.CONTINUE,
    // todo replace with rate limiter
    private val batchFetchDelay: Duration = 5.seconds,
    private val pageFetchDelay: Duration = 5.seconds
) : ExternalFetcher<T> {

    private val logger by LoggerProperty()
    private val fetcherFailover: FetcherFailover = FetcherFailover.of(failoverStrategy)

    override fun fetchAds(): Flow<T> = flow {
        while (true) {
            fetchBatch(this)
            delay(batchFetchDelay)
        }
    }

    private suspend fun fetchBatch(collector: FlowCollector<T>) {
        var pageNum = 1

        // used just for testing
        val lastHandledAdTimeBeforeBatch = lastHandledAdTimestampProvider.provide() ?: Instant.now()
        var lastHandledAdTimeInBatch = lastHandledAdTimeBeforeBatch

        try {
            do {
                val fetchResult = fetchPage(collector, pageNum, lastHandledAdTimeBeforeBatch)
                lastHandledAdTimeInBatch = maxOf(lastHandledAdTimeInBatch, fetchResult.lastAdTimestamp)
                lastHandledAdTimestampProvider.update(lastHandledAdTimeInBatch)
                when (fetchResult) {
                    is FetchResult.NextPageRequired -> ++pageNum
                    is FetchResult.Completed -> break
                }
                delay(pageFetchDelay)
            } while (true)
        } catch (exception: Exception) {
            logger.error("Error during fetching page from ss.ge", exception)
            fetcherFailover.onPageFail(exception)
        }
    }

    protected fun <T, R> Flow<T>.mapWithFailover(transform: suspend (value: T) -> R): Flow<R> = transform { value ->
        try {
            return@transform emit(transform(value))
        } catch (exception: Exception) {
            logger.error("Error during fetching ad from ss.ge", exception)
            fetcherFailover.onElementFail(exception)
        }
    }

    protected abstract suspend fun fetchPage(
        collector: FlowCollector<T>,
        page: Int,
        lastHandledAdTime: Instant
    ): FetchResult

    sealed class FetchResult(val lastAdTimestamp: Instant) {

        class Completed(lastAdTimestamp: Instant) : FetchResult(lastAdTimestamp)
        class NextPageRequired(lastAdTimestamp: Instant) : FetchResult(lastAdTimestamp)
    }
}



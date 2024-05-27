package exe.tigrulya.relohome.fetcher.runner

import exe.tigrulya.relohome.fetcher.ExternalFetcher

// todo add strategy to start search from last offset
enum class FetcherFailoverStrategy {
    FAIL,
    CONTINUE,
    RECREATE_FETCHER
}

sealed interface FetcherFailover {
    companion object {
        fun of(strategy: FetcherFailoverStrategy): FetcherFailover = when (strategy) {
            FetcherFailoverStrategy.FAIL -> FailingFetcherFailover
            FetcherFailoverStrategy.CONTINUE -> NoOpFailover
            FetcherFailoverStrategy.RECREATE_FETCHER -> RecreateFetcherFailover
        }
    }

    fun <T> failover(
        exception: Exception,
        currentFetcher: ExternalFetcher<T>,
        fetcherFactory: FetcherFactory<T>): ExternalFetcher<T>
}

data object FailingFetcherFailover : FetcherFailover {
    override fun <T> failover(exception: Exception,
                              currentFetcher: ExternalFetcher<T>,
                              fetcherFactory: FetcherFactory<T>): ExternalFetcher<T> {
        throw exception
    }
}

data object NoOpFailover : FetcherFailover {
    override fun <T> failover(exception: Exception,
                              currentFetcher: ExternalFetcher<T>,
                              fetcherFactory: FetcherFactory<T>): ExternalFetcher<T> {
        return currentFetcher
    }
}

data object RecreateFetcherFailover : FetcherFailover {
    override fun <T> failover(exception: Exception,
                              currentFetcher: ExternalFetcher<T>,
                              fetcherFactory: FetcherFactory<T>): ExternalFetcher<T> {
        return fetcherFactory.invoke()
    }
}
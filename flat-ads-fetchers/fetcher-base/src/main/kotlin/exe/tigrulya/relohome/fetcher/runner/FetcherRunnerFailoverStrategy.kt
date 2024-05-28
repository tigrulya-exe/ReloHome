package exe.tigrulya.relohome.fetcher.runner

import exe.tigrulya.relohome.fetcher.ExternalFetcher

// todo add strategy to start search from last offset
enum class FetcherRunnerFailoverStrategy {
    FAIL,
    CONTINUE,
    RECREATE_FETCHER
}

sealed interface FetcherRunnerFailover {
    companion object {
        fun of(strategy: FetcherRunnerFailoverStrategy): FetcherRunnerFailover = when (strategy) {
            FetcherRunnerFailoverStrategy.FAIL -> FailingFetcherRunnerFailover
            FetcherRunnerFailoverStrategy.CONTINUE -> NoOpRunnerFailover
            FetcherRunnerFailoverStrategy.RECREATE_FETCHER -> RecreateFetcherRunnerFailover
        }
    }

    fun <T> onFetcherFail(
        exception: Exception,
        currentFetcher: ExternalFetcher<T>,
        fetcherFactory: FetcherFactory<T>): ExternalFetcher<T>
}

data object FailingFetcherRunnerFailover : FetcherRunnerFailover {
    override fun <T> onFetcherFail(exception: Exception,
                                   currentFetcher: ExternalFetcher<T>,
                                   fetcherFactory: FetcherFactory<T>): ExternalFetcher<T> {
        throw exception
    }
}

data object NoOpRunnerFailover : FetcherRunnerFailover {
    override fun <T> onFetcherFail(exception: Exception,
                                   currentFetcher: ExternalFetcher<T>,
                                   fetcherFactory: FetcherFactory<T>): ExternalFetcher<T> {
        return currentFetcher
    }
}

data object RecreateFetcherRunnerFailover : FetcherRunnerFailover {
    override fun <T> onFetcherFail(exception: Exception,
                                   currentFetcher: ExternalFetcher<T>,
                                   fetcherFactory: FetcherFactory<T>): ExternalFetcher<T> {
        return fetcherFactory.invoke()
    }
}
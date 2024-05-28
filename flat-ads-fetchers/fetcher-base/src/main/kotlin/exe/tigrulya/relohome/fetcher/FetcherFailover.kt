package exe.tigrulya.relohome.fetcher

enum class FetcherFailoverStrategy {
    FAIL,
    CONTINUE
}

sealed interface FetcherFailover {
    companion object {
        fun of(strategy: FetcherFailoverStrategy): FetcherFailover = when (strategy) {
            FetcherFailoverStrategy.FAIL -> FailingFetcherFailover
            FetcherFailoverStrategy.CONTINUE -> NoOpFetcherFailover
        }
    }

    fun onPageFail(exception: Exception)
    fun onElementFail(exception: Exception)
}

data object FailingFetcherFailover : FetcherFailover {
    override fun onPageFail(exception: Exception) {
        throw exception
    }

    override fun onElementFail(exception: Exception) {
        throw exception
    }
}

data object NoOpFetcherFailover : FetcherFailover {
    override fun onPageFail(exception: Exception) {
    }

    override fun onElementFail(exception: Exception) {
    }
}
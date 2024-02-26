package exe.tigrulya.relohome.api

import exe.tigrulya.relohome.model.City
import exe.tigrulya.relohome.model.UserCreateDto
import exe.tigrulya.relohome.model.UserSearchOptionsDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

interface UserHandlerGateway {
    suspend fun registerUser(user: UserCreateDto)

    suspend fun setLocation(externalId: String, city: City)

    suspend fun setSearchOptions(externalId: String, searchOptions: UserSearchOptionsDto)

    suspend fun toggleSearch(externalId: String): Boolean
}

typealias ErrorCallback<V> = (Throwable) -> V

class BlockingUserHandlerGateway(
    private val delegate: UserHandlerGateway,
    private val context: CoroutineContext
) {
    companion object {
        private val ThrowingErrorCallback: ErrorCallback<Nothing> = { throw it }

        fun wrap(
            userHandlerGateway: UserHandlerGateway,
            context: CoroutineContext = Dispatchers.Default
        ) = AsyncUserHandlerGateway(userHandlerGateway, context)
    }

    fun registerUser(
        user: UserCreateDto
    ): Result<Unit> = runBlocking {
            runCatching { delegate.registerUser(user) }
    }

    fun setLocation(
        externalId: String,
        city: City,
    ): Result<Unit> = runBlocking {
        runCatching { delegate.setLocation(externalId, city) }
    }

    fun setSearchOptions(
        externalId: String,
        searchOptions: UserSearchOptionsDto,
    ): Result<Unit> = runBlocking {
        runCatching { delegate.setSearchOptions(externalId, searchOptions) }
    }

    fun toggleSearch(
        externalId: String
    ): Result<Boolean> = runBlocking(context) {
        runCatching { delegate.toggleSearch(externalId) }
    }
}


// todo use coroutine friendly tg framework
class AsyncUserHandlerGateway(
    private val delegate: UserHandlerGateway,
    private val context: CoroutineContext
) {
    private val coroutineScope: CoroutineScope = CoroutineScope(context)

    companion object {
        private val ThrowingErrorCallback: ErrorCallback<Nothing> = { throw it }

        fun wrap(
            userHandlerGateway: UserHandlerGateway,
            context: CoroutineContext = Dispatchers.Default
        ) = AsyncUserHandlerGateway(userHandlerGateway, context)
    }

    fun registerUser(
        user: UserCreateDto,
        onError: ErrorCallback<Void> = ThrowingErrorCallback
    ) = coroutineScope.launch {
        runCatching { delegate.registerUser(user) }
            .getOrElse { onError(it) }
    }

    fun setLocation(
        externalId: String,
        city: City,
        onError: ErrorCallback<Void> = ThrowingErrorCallback
    ) = coroutineScope.launch {
        runCatching {
            delegate.setLocation(externalId, city)
        }.getOrElse { onError(it) }
    }

    fun setSearchOptions(
        externalId: String,
        searchOptions: UserSearchOptionsDto,
        onError: ErrorCallback<Void> = ThrowingErrorCallback
    ) = coroutineScope.launch {
        runCatching {
            delegate.setSearchOptions(externalId, searchOptions)
        }.getOrElse { onError(it) }
    }

    // todo use coroutine friendly tg framework
    fun toggleSearch(
        externalId: String,
        onError: ErrorCallback<Boolean> = ThrowingErrorCallback
    ): Boolean = runBlocking(context) {
        runCatching {
            delegate.toggleSearch(externalId)
        }.getOrElse { onError(it) }
    }
}



package exe.tigrulya.relohome.handler.cache

import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.RedisClient
import io.lettuce.core.SetArgs
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.coroutines
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import kotlin.time.Duration
import kotlin.time.toJavaDuration

@OptIn(ExperimentalLettuceCoroutinesApi::class)
class RedisHandledAdsCache(
    redisUrl: String,
    keyTimeout: Duration
): HandledAdsCache {
    // todo close resources
    private val redisClient: RedisClient = RedisClient.create(redisUrl)
    private val connection: StatefulRedisConnection<String, String> = redisClient.connect()
    private val redisApi: RedisCoroutinesCommands<String, String> = connection.coroutines()

    private val keyTtl = keyTimeout.toJavaDuration()
    private val setTtlArgs: SetArgs = SetArgs().px(keyTtl)

    companion object {
        const val COMPOSITE_KEY_DELIMITER = "_"
        const val VALUE = "0"
    }

    override suspend fun contains(fetcherId: String, adId: String): Boolean {
        val key = key(fetcherId, adId)

        return redisApi.get(key)?.let {
            updateKeyTtl(key)
            true
        } ?: false
    }

    override suspend fun insert(fetcherId: String, adId: String) {
        insert(key(fetcherId, adId))
    }

    private suspend fun insert(key: String) {
        redisApi.set(key, VALUE, setTtlArgs)
    }

    private suspend fun updateKeyTtl(key: String) {
        redisApi.pexpire(key, keyTtl)
    }

    private fun key(fetcherId: String, adId: String) = fetcherId + COMPOSITE_KEY_DELIMITER + adId
}
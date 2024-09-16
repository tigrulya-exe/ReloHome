package exe.tigrulya.relohome.notifier.telegram.bot.state.repo

import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.coroutines
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands

@OptIn(ExperimentalLettuceCoroutinesApi::class)
class RedisTgStateRepository(
    redisUrl: String
) : UserStatesRepository,
    UserLocalesRepository {
    companion object {
        const val STATE_KEY = "state"
        const val LOCALE_KEY = "locale"
    }

    private val redisClient: RedisClient = RedisClient.create(redisUrl)
    private val connection: StatefulRedisConnection<String, String> = redisClient.connect()
    private val redisApi: RedisCoroutinesCommands<String, String> = connection.coroutines()

    override suspend fun getState(userId: String): UserState? {
        return redisApi.hget(userId, STATE_KEY)?.let {
            UserState.valueOf(it)
        }
    }

    override suspend fun setState(userId: String, state: UserState) {
        redisApi.hset(userId, STATE_KEY, state.name)
    }

    override suspend fun getLocale(userId: String): String? {
        return redisApi.hget(userId, LOCALE_KEY)
    }

    override suspend fun setLocale(userId: String, locale: String) {
        redisApi.hset(userId, LOCALE_KEY, locale)
    }
}

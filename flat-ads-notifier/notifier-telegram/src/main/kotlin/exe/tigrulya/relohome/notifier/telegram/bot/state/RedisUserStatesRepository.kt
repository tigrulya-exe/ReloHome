package exe.tigrulya.relohome.notifier.telegram.bot.state

import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.coroutines
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands

@OptIn(ExperimentalLettuceCoroutinesApi::class)
class RedisUserStatesRepository(
    redisUrl: String
) : UserStatesRepository {

    private val redisClient: RedisClient = RedisClient.create(redisUrl)
    private val connection: StatefulRedisConnection<String, String> = redisClient.connect()
    private val redisApi: RedisCoroutinesCommands<String, String> = connection.coroutines()

    override suspend fun get(userId: String): UserState? {
        return redisApi.get(userId)?.let { UserState.valueOf(userId) }
    }

    override suspend fun set(userId: String, state: UserState) {
        redisApi.set(userId, state.name)
    }
}

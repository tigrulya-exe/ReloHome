package exe.tigrulya.relohome.fetcher

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.nio.ByteBuffer
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Duration
import java.time.Instant
import kotlin.concurrent.thread
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes

interface LastHandledAdTimestampProvider {
    fun provide(): Instant?

    fun update(newVal: Instant)
}

class NowTimestampProvider : LastHandledAdTimestampProvider {
    override fun provide(): Instant = Instant.now()

    override fun update(newVal: Instant) {}
}

class WindowTillNowTimestampProvider(
    private val amountToSubtract: Duration,
) : LastHandledAdTimestampProvider {
    private var timestamp: Instant? = null

    override fun provide(): Instant? {
        timestamp = timestamp ?: Instant.now().minus(amountToSubtract)
        return timestamp
    }

    override fun update(newVal: Instant) {
        timestamp = newVal
    }
}

class FileBackedLastHandledAdTimestampProvider(
    private val snapshotFilePath: Path = Paths.get("last_ad.timestamp"),
    private val delegateTimestampProvider: LastHandledAdTimestampProvider = NowTimestampProvider()
) : LastHandledAdTimestampProvider, AutoCloseable {
    private var timestamp: Instant? = null
    private val timestampChannel = Channel<Instant>(
        capacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    companion object {
        fun serializeTimestamp(timestamp: Instant, path: Path) {
            val bytes = ByteBuffer.allocate(Long.SIZE_BYTES)
                .putLong(timestamp.toEpochMilli())
                .array()
            path.writeBytes(bytes)
        }

        fun deserializeTimestamp(path: Path): Instant {
            val epochMilli = ByteBuffer.wrap(path.readBytes()).getLong()
            return Instant.ofEpochMilli(epochMilli)
        }
    }

    init {
        if (!snapshotFilePath.exists()) {
            snapshotFilePath.createFile()
        } else {
            timestamp = deserializeTimestamp(snapshotFilePath)
        }
        // todo lol?
        thread {
            runBlocking {
                launch(Dispatchers.IO) {
                    for (timestamp in timestampChannel) {
                        serializeTimestamp(timestamp, snapshotFilePath)
                    }
                }
            }
        }
    }

    override fun provide(): Instant? = timestamp ?: delegateTimestampProvider.provide()

    override fun update(newVal: Instant) {
        timestamp = newVal
        timestampChannel.trySend(newVal)
    }

    override fun close() {
        timestampChannel.close()
    }
}

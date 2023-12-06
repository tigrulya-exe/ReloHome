package exe.tigrulya.relohome.fetcher

import java.nio.ByteBuffer
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import java.time.temporal.TemporalUnit
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes
import kotlin.time.Duration

interface LastHandledAdTimestampProvider {
    fun provide(): Instant?

    fun update(newVal: Instant)
}

class InMemoryTimestampProvider(private val serviceId: String) : LastHandledAdTimestampProvider {
    companion object {
        private val timestamps: MutableMap<String, Instant> = mutableMapOf()
    }

    override fun provide(): Instant? = timestamps[serviceId]

    override fun update(newVal: Instant) {
        timestamps[serviceId] = newVal
    }
}

class WindowTillNowTimestampProvider(
    private val amountToSubtract: Long,
    private val unit: TemporalUnit
) : LastHandledAdTimestampProvider {
    private var timestamp: Instant? = null

    override fun provide(): Instant? {
        timestamp = timestamp ?: Instant.now().minus(amountToSubtract, unit)
        return timestamp
    }

    override fun update(newVal: Instant) {
        timestamp = newVal
    }

}

class FileBackedLastHandledAdTimestampProvider(
    private val snapshotFilePath: Path = Paths.get("last_ad.timestamp"),
    snapshotPeriod: Duration
) : LastHandledAdTimestampProvider, AutoCloseable {
    private var timestamp: Instant? = null
    private val timer: Timer = Timer("Last handled timestamp snapshot file updater timer", true)

    companion object {
        fun serializeTimestamp(timestamp: Instant?, path: Path) {
            timestamp?.let {
                val bytes = ByteBuffer.allocate(Long.SIZE_BYTES)
                    .putLong(it.toEpochMilli())
                    .array()
                path.writeBytes(bytes)
            }
        }

        fun deserializeTimestamp(path: Path): Instant? {
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
        timer.scheduleAtFixedRate(0L, snapshotPeriod.inWholeMilliseconds) {
            serializeTimestamp(timestamp, snapshotFilePath)
        }
    }

    override fun provide(): Instant? = timestamp

    override fun update(newVal: Instant) {
        timestamp = newVal
    }

    override fun close() {
        timer.cancel()
    }
}

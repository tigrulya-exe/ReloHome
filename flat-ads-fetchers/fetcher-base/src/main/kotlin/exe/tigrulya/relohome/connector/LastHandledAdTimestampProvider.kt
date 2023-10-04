package exe.tigrulya.relohome.connector

import java.time.Instant
import java.time.temporal.TemporalUnit

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
    var timestamp: Instant? = null

    override fun provide(): Instant? {
        timestamp = timestamp ?: Instant.now().minus(amountToSubtract, unit)
        return timestamp
    }

    override fun update(newVal: Instant) {
        timestamp = newVal
    }

}


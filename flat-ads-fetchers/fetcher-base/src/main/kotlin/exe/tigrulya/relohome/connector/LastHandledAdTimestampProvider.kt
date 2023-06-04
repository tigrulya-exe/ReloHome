package exe.tigrulya.relohome.connector

import java.time.Instant
import java.time.temporal.ChronoUnit
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
    private val unit: TemporalUnit) : LastHandledAdTimestampProvider {
    override fun provide(): Instant = Instant.now()
        .minus(amountToSubtract, unit)

    override fun update(newVal: Instant) {}

}


package exe.tigrulya.relohome.parser

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatterBuilder
import java.util.*

class ListAmDomParserTest : FunSpec ({
    test("check date parser") {
        val testStrInstant = "Sunday, May 28, 2023 14:35"
        val zoneId = ZoneId.of("Asia/Yerevan")

        val format = DateTimeFormatterBuilder()
            .appendPattern("EEEE, MMMM dd, yyyy HH:mm")
            .toFormatter()
            .withZone(zoneId)
            .withLocale(Locale.US)

        val parsedInstant = format.parse(testStrInstant, Instant::from)

        val expectedDate = LocalDateTime.of(2023, 5, 28, 14, 35)
        parsedInstant shouldBe expectedDate.atZone(zoneId).toInstant()
    }
})
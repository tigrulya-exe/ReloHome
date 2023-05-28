package exe.tigrulya.relohome.connector.listam.parser

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatterBuilder
import java.util.*

interface DomParser {
    fun parse(document: Document): Any
}

class ListAmDomParser(
    instantFormat: String = "EEEE, MMMM dd, yyyy HH:mm",
    private val zoneId: ZoneId = ZoneId.of("Asia/Yerevan")
) : DomParser {

    private val dateTimeFormatter = DateTimeFormatterBuilder()
        .appendPattern(instantFormat)
        .toFormatter()
        .withZone(zoneId)
        .withLocale(Locale.US)

    private fun parseListAmFlatAdInfos(body: Element): List<ListAmFlatAdInfo> {
        return body
            .select("#contentr > .dl > a")
            .toList()
            .map { parseListAmFlatAdInfo(it) }
    }

    private fun parseListAmFlatAdInfo(element: Element): ListAmFlatAdInfo {
        val lastModifiedStr = element.selectFirst(".d")?.text()
        val lastModified = dateTimeFormatter.parse(lastModifiedStr, Instant::from)

        return ListAmFlatAdInfo(
            id = element.attr("href").split("/").last(),
            lastModified = lastModified
        )
    }

    private fun parseListAmFlatAd(element: Element): ListAmFlatAd {
        val elementMap = mutableMapOf<String, String>()
        var lastKey = ""

        val baseNode = element.selectFirst("#pcontent")!!

        for (node in baseNode.select(".vi div[class^=attr] .c div").toList()) {
            if (node.className() == "t") {
                lastKey = node.text()
                continue
            }

            elementMap[lastKey] = node.text()
        }

        // todo get other currencies via element.selectFirst("div[class^=price]")
        val priceSpan = baseNode.selectFirst("span[class^=price]")
        val price = priceSpan?.attr("content")
        val currency = priceSpan?.selectFirst("meta[itemprop=priceCurrency]")
            ?.attr("content")

        val id = baseNode.selectFirst("div[class^=footer] span")?.text()?.removePrefix("Номер объявления ")!!
        val address = baseNode.selectFirst(".loc")?.text()
        val imgUrl = baseNode.selectFirst(".p img")?.attr("src")

        return ListAmFlatAd(
            id = id,
            price = price,
            address = address,
            imgUrl = imgUrl
        )
    }

    override fun parse(document: Document): Any {
        val mainPageLink = document.head()
            .selectFirst("link[rel=canonical][href$=/category/56]")

        return mainPageLink?.let {
            parseListAmFlatAdInfos(document.body())
        } ?: parseListAmFlatAd(document.body())
    }
}
package exe.tigrulya.relohome.ssge.parser

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import exe.tigrulya.relohome.fetcher.HtmlDomParser
import exe.tigrulya.relohome.ssge.model.SsGeFlatAdContainer
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class SsGeDomParser : HtmlDomParser<Any> {

    private val objectMapper = jacksonObjectMapper().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        registerModule(JavaTimeModule())
    }

    private fun parseFlatAd(element: Element): SsGeFlatAdContainer {
        // TODO handle not found
        val dataJson = element.selectFirst("#__NEXT_DATA__")!!.data()
        val flatAdNode = objectMapper.readTree(dataJson).at("/props/pageProps")
        return objectMapper.treeToValue(flatAdNode)
    }

    override fun parse(document: Document): Any {
        return parseFlatAd(document)
    }
}
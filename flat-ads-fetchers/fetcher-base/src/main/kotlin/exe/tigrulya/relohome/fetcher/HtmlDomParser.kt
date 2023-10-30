package exe.tigrulya.relohome.fetcher

import org.jsoup.nodes.Document

interface HtmlDomParser<T> {
    fun parse(document: Document): T
}
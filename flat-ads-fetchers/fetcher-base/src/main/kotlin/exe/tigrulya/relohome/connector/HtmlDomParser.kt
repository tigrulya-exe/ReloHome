package exe.tigrulya.relohome.connector

import org.jsoup.nodes.Document

interface HtmlDomParser<T> {
    fun parse(document: Document): T
}
package exe.tigrulya.relohome.connector.listam.fetcher

import exe.tigrulya.relohome.connector.ExternalSiteClient
import exe.tigrulya.relohome.connector.listam.parser.ListAmDomParser
import exe.tigrulya.relohome.connector.listam.parser.ListAmFlatAd
import exe.tigrulya.relohome.connector.listam.parser.ListAmFlatAdInfo
import io.ktor.client.call.*
import io.ktor.client.request.*

class ListAmClient(baseUrl: String) :
    ExternalSiteClient<Any>(baseUrl, ListAmDomParser()) {
    suspend fun fetchAds(pageNum: Int = 1): List<ListAmFlatAdInfo> {
        return httpClient
            .get {
                url("/en/category/56/$pageNum")
                header("Cookie", "gl=2;")
                header("Host", "www.list.am")
            }
            .body()
    }

    suspend fun fetchAd(id: String): ListAmFlatAd {
        return httpClient
            .get("/ru/item/$id")
            .body()
    }

}
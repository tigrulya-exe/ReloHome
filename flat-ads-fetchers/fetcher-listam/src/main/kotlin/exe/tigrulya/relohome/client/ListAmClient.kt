package exe.tigrulya.relohome.client

import exe.tigrulya.relohome.connector.ExternalParseableSiteClient
import exe.tigrulya.relohome.parser.ListAmDomParser
import exe.tigrulya.relohome.parser.ListAmFlatAd
import exe.tigrulya.relohome.parser.ListAmFlatAdInfo
import io.ktor.client.call.*
import io.ktor.client.request.*

class ListAmClient(baseUrl: String) :
    ExternalParseableSiteClient<Any>(baseUrl, ListAmDomParser()) {
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
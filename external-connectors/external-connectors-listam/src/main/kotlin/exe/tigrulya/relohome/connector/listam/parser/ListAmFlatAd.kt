package exe.tigrulya.relohome.connector.listam.parser

import java.time.Instant

data class ListAmFlatAdInfo(
    val id: String,
    val lastModified: Instant
)

data class ListAmFlatAd(
    val id: String,
    val price: String?,
    val address: String?,
    val imgUrl: String?
)
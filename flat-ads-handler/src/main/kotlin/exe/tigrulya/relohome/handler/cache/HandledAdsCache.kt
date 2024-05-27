package exe.tigrulya.relohome.handler.cache

interface HandledAdsCache {
    suspend fun contains(fetcherId: String, adId: String): Boolean
    suspend fun insert(fetcherId: String, adId: String)
}
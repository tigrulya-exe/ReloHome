package exe.tigrulya.relohome.connector

import exe.tigrulya.relohome.connector.model.FlatAd

interface FlatAdsCollector {
    fun collect(flatAd: FlatAd)
}
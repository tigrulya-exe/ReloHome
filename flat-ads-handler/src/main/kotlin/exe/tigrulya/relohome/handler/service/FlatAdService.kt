package exe.tigrulya.relohome.handler.service

import exe.tigrulya.relohome.api.FlatAdHandlerGateway
import exe.tigrulya.relohome.api.FlatAdNotifierGateway
import exe.tigrulya.relohome.handler.cache.HandledAdsCache
import exe.tigrulya.relohome.handler.repository.SearchOptions
import exe.tigrulya.relohome.handler.repository.SubDistricts
import exe.tigrulya.relohome.handler.repository.Users
import exe.tigrulya.relohome.model.FlatAd
import exe.tigrulya.relohome.model.UserInfo
import exe.tigrulya.relohome.util.LoggerProperty
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class FlatAdService(
    private val notifierGateway: FlatAdNotifierGateway,
    // todo replace direct dependency injection with streams (aka coroutine flows)
    private val handledAdsCache: HandledAdsCache
) : FlatAdHandlerGateway {

    private val logger by LoggerProperty()

    override suspend fun handle(flatAd: FlatAd) {
        val flatAdReceivers = getUserExternalIdsForFlatAd(flatAd)
        if (flatAdReceivers.isEmpty()) {
            return
        }

        if (handledAdsCache.contains(flatAd.serviceId, flatAd.id)) {
            logger.info("Skipping already handled ad: ${flatAd.id} from ${flatAd.serviceId}")
            return
        }

        logger.debug("Send {} to {}", flatAd.id, flatAdReceivers)
        notifierGateway.onNewAd(flatAdReceivers, flatAd)
        handledAdsCache.insert(flatAd.serviceId, flatAd.id)
    }

    suspend fun getDistricts(cityName: String): List<String> = newSuspendedTransaction(Dispatchers.IO) {
        SubDistricts.getByCityName(cityName)
    }

    private suspend fun getUserExternalIdsForFlatAd(
        flatAd: FlatAd
    ): List<UserInfo> = newSuspendedTransaction(Dispatchers.IO) {

        // addLogger(StdOutSqlLogger)

        val query = SearchOptions
            .join(Users, JoinType.INNER, onColumn = SearchOptions.externalId, otherColumn = Users.externalId)
            .slice(SearchOptions.externalId, Users.name, Users.locale)
            .select(SearchOptions.cityName eq flatAd.address.city.name)
            .andWhere { SearchOptions.enabled eq true }
            .andWhere {
                (SearchOptions.floorTo.isNull() or (SearchOptions.floorTo greaterEq flatAd.info.floor)) and
                        (SearchOptions.floorFrom.isNull() or (SearchOptions.floorFrom lessEq flatAd.info.floor))
            }


        // todo mb it would be better to use prepared statement here
        flatAd.info.rooms?.let {
            query.andWhere {
                (SearchOptions.roomsTo.isNull() or (SearchOptions.roomsTo greaterEq it)) and
                        (SearchOptions.roomsFrom.isNull() or (SearchOptions.roomsFrom lessEq it))
            }
        }

        flatAd.info.bedrooms?.let {
            query.andWhere {
                (SearchOptions.bedroomsTo.isNull() or (SearchOptions.bedroomsTo greaterEq it)) and
                        (SearchOptions.bedroomsFrom.isNull() or (SearchOptions.bedroomsFrom lessEq it))
            }
        }

        flatAd.info.spaceSquareMeters?.let {
            query.andWhere {
                (SearchOptions.areaTo.isNull() or (SearchOptions.areaTo greaterEq it)) and
                        (SearchOptions.areaFrom.isNull() or (SearchOptions.areaFrom lessEq it))
            }
        }

        flatAd.price?.amount?.let {
            query.andWhere {
                (SearchOptions.priceTo.isNull() or (SearchOptions.priceTo greaterEq it)) and
                        (SearchOptions.priceFrom.isNull() or (SearchOptions.priceFrom lessEq it))
            }
        }

        flatAd.address.subDistrict?.let {
            query.andWhere {
                SearchOptions.subDistricts.isNull() or
                        (SearchOptions.subDistricts like "%${it.lowercase()}%")
            }
        }

        query.map {
            UserInfo(
                id = it[SearchOptions.externalId],
                name = it[Users.name],
                locale = it[Users.locale]
            )
        }
    }
}
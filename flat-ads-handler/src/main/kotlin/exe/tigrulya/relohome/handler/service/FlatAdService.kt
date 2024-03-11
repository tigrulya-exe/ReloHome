package exe.tigrulya.relohome.handler.service

import exe.tigrulya.relohome.api.FlatAdHandlerGateway
import exe.tigrulya.relohome.api.FlatAdNotifierGateway
import exe.tigrulya.relohome.handler.repository.SubDistricts
import exe.tigrulya.relohome.handler.repository.SearchOptions
import exe.tigrulya.relohome.model.FlatAd
import exe.tigrulya.relohome.util.LoggerProperty
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class FlatAdService(
    private val notifierGateway: FlatAdNotifierGateway
) : FlatAdHandlerGateway {

    private val logger by LoggerProperty()

    override suspend fun handle(flatAd: FlatAd) {
        val flatAdReceivers = getUserExternalIdsForFlatAd(flatAd)
        if (flatAdReceivers.isNotEmpty()) {
            logger.info("Send ${flatAd.id} to $flatAdReceivers")
            notifierGateway.onNewAd(flatAdReceivers, flatAd)
        }
    }

    fun getDistricts(cityName: String): List<String> = transaction {
        SubDistricts.getByCityName(cityName)
    }

    private fun getUserExternalIdsForFlatAd(flatAd: FlatAd): List<String> = transaction {

        addLogger(StdOutSqlLogger)

        val query = SearchOptions
            .slice(SearchOptions.externalId)
            .select(SearchOptions.cityName eq flatAd.address.city.name)
            .andWhere { SearchOptions.enabled eq true }

        // todo mb it would be better to use prepared statement here
        flatAd.info.rooms?.let {
            query.andWhere {
                (SearchOptions.roomsTo.isNull() or (SearchOptions.roomsTo greaterEq it)) and
                        (SearchOptions.roomsFrom.isNull() or (SearchOptions.roomsFrom lessEq it))
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

        query.map { it[SearchOptions.externalId] }
    }
}
package exe.tigrulya.relohome.handler.service

import exe.tigrulya.relohome.api.FlatAdHandlerGateway
import exe.tigrulya.relohome.api.FlatAdNotifierGateway
import exe.tigrulya.relohome.handler.repository.SubDistricts
import exe.tigrulya.relohome.handler.repository.UserSearchOptions
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

        val query = UserSearchOptions
            .slice(UserSearchOptions.externalId)
            .select(UserSearchOptions.cityName eq flatAd.address.city.name)

        // todo mb it would be better to use prepared statement here
        flatAd.info.rooms?.let {
            query.andWhere {
                (UserSearchOptions.roomsTo.isNull() or (UserSearchOptions.roomsTo greaterEq it)) and
                        (UserSearchOptions.roomsFrom.isNull() or (UserSearchOptions.roomsFrom lessEq it))
            }
        }

        flatAd.price?.amount?.let {
            query.andWhere {
                (UserSearchOptions.priceTo.isNull() or (UserSearchOptions.priceTo greaterEq it)) and
                        (UserSearchOptions.priceFrom.isNull() or (UserSearchOptions.priceFrom lessEq it))
            }
        }

        flatAd.address.subDistrict?.let {
            query.andWhere {
                UserSearchOptions.subDistricts.isNull() or
                        (UserSearchOptions.subDistricts like "%${it.lowercase()}%")
            }
        }

        query.map { it[UserSearchOptions.externalId] }
    }
}
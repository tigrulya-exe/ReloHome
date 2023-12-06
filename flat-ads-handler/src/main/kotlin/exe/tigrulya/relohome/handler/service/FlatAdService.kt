package exe.tigrulya.relohome.handler.service

import exe.tigrulya.relohome.api.FlatAdHandlerGateway
import exe.tigrulya.relohome.api.FlatAdNotifierGateway
import exe.tigrulya.relohome.handler.repository.UserSearchOptions
import exe.tigrulya.relohome.model.City
import exe.tigrulya.relohome.model.FlatAd
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class FlatAdService(
    private val notifierGateway: FlatAdNotifierGateway
) : FlatAdHandlerGateway {
    override fun handle(flatAd: FlatAd) {
        getUserExternalIdsForFlatAd(flatAd)
            .forEach { notifierGateway.onNewAd(it, flatAd) }
    }

    // todo get this info from db
    // todo replace string with city object
    fun getDistricts(cityName: String): List<String> {
        return if (cityName.lowercase() == "tbilisi") {
            listOf(
                "avlabari",
                "bagebi",
                "chugureti",
                "didiDigomi",
                "didube",
                "digomi",
                "gldani",
                "isani",
                "mtatsminda",
                "nadzaladevi",
                "ortachala",
                "samgori",
                "saburtalo",
                "sololaki",
                "vake",
                "varketili",
                "vera"
            )
        } else {
            listOf()
        }
    }

    // todo mb use native queries to increase performance
    private fun getUserExternalIdsForFlatAd(flatAd: FlatAd): List<String> = transaction {

        addLogger(StdOutSqlLogger)

        val query = UserSearchOptions
            .slice(UserSearchOptions.externalId)
            .select(UserSearchOptions.cityName eq flatAd.address.city.name)

        flatAd.info.rooms?.let {
            query.andWhere {
                (UserSearchOptions.roomsTo.isNull() or (UserSearchOptions.roomsTo greaterEq it)) and
                        (UserSearchOptions.roomsFrom.isNull() or (UserSearchOptions.roomsFrom lessEq it))
            }
        }

        flatAd.price?.amount?.let {
            query.andWhere {
                (UserSearchOptions.priceTo.isNull() or (UserSearchOptions.priceTo greaterEq it)) and
                        (UserSearchOptions.priceTo.isNull() or (UserSearchOptions.priceFrom lessEq it))
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
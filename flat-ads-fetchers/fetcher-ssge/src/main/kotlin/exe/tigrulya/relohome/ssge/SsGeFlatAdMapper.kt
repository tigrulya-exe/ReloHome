package exe.tigrulya.relohome.ssge

import exe.tigrulya.relohome.fetcher.FlatAdMapper
import exe.tigrulya.relohome.model.*
import exe.tigrulya.relohome.ssge.model.FlatAdImageContainer
import exe.tigrulya.relohome.ssge.model.SsGeFlatAd

class SsGeFlatAdMapper : FlatAdMapper<SsGeFlatAd> {
    override fun toFlatAd(externalFlatAd: SsGeFlatAd): FlatAd {
        val address = with(externalFlatAd.address) {
            Address(
                city = City(
                    name = "Tbilisi",
                    country = "Georgia"
                ),
                district = districtTitle,
                subDistrict = subdistrictTitle,
                street = streetTitle,
                building = streetNumber
                //TODO add here location parse from gmaps from description
            )
        }

        val price = with(externalFlatAd.price) {
            Price(
                amount = this.priceUsd,
                currency = Price.Currency.USD
            )
        }

        val flatInfo = FlatInfo(
            floor = externalFlatAd.floorNumber.toInt(),
            totalFloors = externalFlatAd.totalAmountOfFloor,
            spaceSquareMeters = externalFlatAd.totalArea,
            // todo
            rooms = externalFlatAd.numberOfBedrooms,
            bedrooms = externalFlatAd.numberOfBedrooms
        )

        val contacts = Contacts(
            flatServiceLink = "https://home.ss.ge/en/real-estate/${externalFlatAd.detailUrl}",
            // todo
            phoneNumber = null
        )

        val pictures = externalFlatAd.appImages
            ?.sortedBy { it.orderNo }
            ?.map { it.toPicture() }
            ?: listOf()

        return FlatAd(
            id = externalFlatAd.applicationId.toString(),
            title = externalFlatAd.title,
            address = address,
            price = price,
            description = externalFlatAd.description,
            info = flatInfo,
            contacts = contacts,
            serviceId = SsGeFetcher.FETCHER_ID,
            images = pictures
        )
    }

    private fun FlatAdImageContainer.toPicture(): Image {
        return Image(fileName.replace("_Thumb", ""))
    }
}

package exe.tigrulya.relohome.ssge

import exe.tigrulya.relohome.fetcher.FlatAdMapper
import exe.tigrulya.relohome.model.*
import exe.tigrulya.relohome.ssge.model.ApplicationPhone
import exe.tigrulya.relohome.ssge.model.FlatAdImageContainer
import exe.tigrulya.relohome.ssge.model.SsGeFlatAd
import exe.tigrulya.relohome.ssge.model.SsGeFlatAdContainer

class SsGeFlatAdMapper(private val maxAdImages: Int = 6) : FlatAdMapper<SsGeFlatAdContainer> {
    override fun toFlatAd(externalFlatAd: SsGeFlatAdContainer): FlatAd = with(externalFlatAd) {
        val address = with(applicationData.address) {
            Address(
                city = City(
                    name = "Tbilisi",
                    country = "Georgia"
                ),
                district = districtTitle,
                subDistrict = subdistrictTitle,
                street = streetTitle,
                building = streetNumber,
                location = getLocation(applicationData)
            )
        }

        val price = with(applicationData.price) {
            Price(
                amount = this.priceUsd,
                currency = Price.Currency.USD
            )
        }

        val flatInfo = FlatInfo(
            floor = applicationData.floor.toInt(),
            totalFloors = applicationData.floors.toInt(),
            spaceSquareMeters = applicationData.totalArea.toInt(),
            rooms = applicationData.rooms,
            bedrooms = applicationData.bedrooms
        )

        val contacts = Contacts(
            flatServiceLink = externalFlatAd.fullUrl,
            phoneNumber = getPhoneNumber(applicationData.applicationPhones)
            // todo add messenger ids
        )

        val pictures = applicationData.appImages
            ?.take(maxAdImages)
            ?.sortedBy { it.orderNo }
            ?.map { it.toPicture() }
            ?: listOf()

        return FlatAd(
            id = applicationData.applicationId.toString(),
            title = applicationData.title,
            address = address,
            price = price,
            description = applicationData.description.text,
            info = flatInfo,
            contacts = contacts,
            serviceId = SsGeFetcher.FETCHER_ID,
            images = pictures
        )
    }

    private fun FlatAdImageContainer.toPicture(): Image {
        return Image(fileName.replace("_Thumb", ""))
    }

    private fun getLocation(flatAd: SsGeFlatAd): Location? {
        if (!isCorrectCoordinate(flatAd.locationLatitude) || !isCorrectCoordinate(flatAd.locationLongitude)) {
            return null
        }
        return Location(flatAd.locationLatitude!!, flatAd.locationLongitude!!)
    }

    private fun isCorrectCoordinate(coordinate: String?): Boolean {
        return coordinate?.isNotBlank() ?: false
    }

    private fun getPhoneNumber(applicationPhones: List<ApplicationPhone>): String? {
        return if (applicationPhones.isEmpty()) {
            null
        } else {
            applicationPhones.first().phoneNumber
        }
    }
}

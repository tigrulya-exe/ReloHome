package exe.tigrulya.relohome.model

data class City(
    val name: String,
    val country: String
)

data class Location(
    val lat: String,
    val lon: String
)

data class Address(
    val city: City,
    val district: String? = null,
    val subDistrict: String? = null,
    val street: String? = null,
    val building: String? = null,
    val location: Location? = null,
    val customAddressString: String? = null,
)

data class Price(
    val amount: Int,
    val currency: Currency = Currency.USD
) {
    enum class Currency {
        USD,
        GEL,
        AMD,
        RUB
    }

    override fun toString(): String {
        return "$amount $currency"
    }
}

data class FlatInfo(
    val floor: Int,
    val totalFloors: Int? = null,
    val spaceSquareMeters: Int? = null,
    val rooms: Int? = null,
    val bedrooms: Int? = null,
    val buildingType: BuildingType? = null,
    val flatType: FlatType? = null
) {
    enum class BuildingType {
        OLD,
        NEW
    }

    enum class FlatType {
        OLD,
        OLD_RENEWED,
        INHABITED
    }
}

data class Contacts(
    val flatServiceLink: String,
    val phoneNumber: String? = null,
    val messengerIds: Map<Messenger, String> = mapOf()
) {
    enum class Messenger {
        VIBER,
        WHATSAPP,
        MESSENGER,
        TELEGRAM,
        VK
    }
}

data class Image(
    val url: String
)

data class FlatAd(
    val id: String,
    val title: String,
    val address: Address,
    val info: FlatInfo,
    val price: Price? = null,
    val description: String? = null,
    val contacts: Contacts,
    val serviceId: String,
    val images: List<Image> = listOf(),
)

data class FlatAdMessage(
    val userIds: List<String>,
    val flatAd: FlatAd
)
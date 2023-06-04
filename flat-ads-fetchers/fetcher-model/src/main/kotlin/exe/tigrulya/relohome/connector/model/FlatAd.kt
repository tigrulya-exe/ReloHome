package exe.tigrulya.relohome.connector.model

data class City(
    val name: String,
    val country: String
)

data class Location(
    val lat: Double,
    val lon: Double
)

data class FlatAd(
    val id: String,
    val price: String,
    val description: String,
    val city: City
)
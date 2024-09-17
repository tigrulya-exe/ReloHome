package exe.tigrulya.relohome.proto

import exe.tigrulya.relohome.api.FlatAdOuterClass
import exe.tigrulya.relohome.model.*
import java.util.function.Consumer

fun FlatAdMessage.toProto(): FlatAdOuterClass.FlatAdMessage = FlatAdOuterClass.FlatAdMessage.newBuilder()
    .also { proto ->
        proto.addAllUsers(users.map { it.toProto() })
        proto.flatAd = flatAd.toProto()
    }.build()

fun FlatAd.toProto(): FlatAdOuterClass.FlatAd = FlatAdOuterClass.FlatAd.newBuilder()
    .also { proto ->
        proto.id = id
        proto.title = title
        proto.address = address.toProto()
        proto.info = info.toProto()
        nullSafeSet(price?.amount, proto::setPriceAmount)
        nullSafeSetEnum(price?.currency, proto::setPriceCurrency)
        proto.putAllDescription(description)
        proto.contacts = contacts.toProto()
        proto.serviceId = serviceId
        proto.addAllImages(images.map { it.url })
    }.build()

fun Address.toProto(): FlatAdOuterClass.Address = FlatAdOuterClass.Address.newBuilder()
    .also { proto ->
        proto.cityName = city.name
        proto.cityCountry = city.country
        nullSafeSet(district, proto::setDistrict)
        nullSafeSet(subDistrict, proto::setSubDistrict)
        nullSafeSet(street, proto::setStreet)
        nullSafeSet(building, proto::setBuilding)
        nullSafeSet(customAddressString, proto::setCustomAddressString)
        nullSafeSet(location?.lat, proto::setLat)
        nullSafeSet(location?.lon, proto::setLon)
    }.build()

fun FlatInfo.toProto(): FlatAdOuterClass.FlatInfo = FlatAdOuterClass.FlatInfo.newBuilder()
    .also { proto ->
        proto.floor = floor
        nullSafeSet(totalFloors, proto::setTotalFloors)
        nullSafeSet(spaceSquareMeters, proto::setSpaceSquareMeters)
        nullSafeSet(rooms, proto::setRooms)
        nullSafeSet(bedrooms, proto::setBedrooms)
        nullSafeSetEnum(buildingType, proto::setBuildingType)
        nullSafeSetEnum(flatType, proto::setFlatType)
    }.build()

fun Contacts.toProto(): FlatAdOuterClass.Contacts = FlatAdOuterClass.Contacts.newBuilder()
    .also { proto ->
        proto.flatServiceLink = flatServiceLink
        nullSafeSet(phoneNumber, proto::setPhoneNumber)
        proto.putAllMessengerIds(messengerIds.mapKeys { it.key.name })
    }.build()

fun UserInfo.toProto(): FlatAdOuterClass.UserInfo = FlatAdOuterClass.UserInfo.newBuilder()
    .also { proto ->
        proto.id = id
        proto.name = name
        nullSafeSet(locale, proto::setLocale)
    }.build()

fun FlatAdOuterClass.FlatAdMessage.toDomain(): FlatAdMessage = FlatAdMessage(
    users = usersList.map { it.toDomain() },
    flatAd = flatAd.toDomain()
)

fun FlatAdOuterClass.FlatAd.toDomain(): FlatAd = FlatAd(
    id = id,
    title = title,
    address = address.toDomain(),
    info = info.toDomain(),
    price = Price(priceAmount, Price.Currency.valueOf(priceCurrency)),
    description = descriptionMap,
    contacts = contacts.toDomain(),
    serviceId = serviceId,
    images = imagesList.map { Image(it) }
)

fun FlatAdOuterClass.Address.toDomain(): Address = Address(
    city = City(cityName, cityCountry),
    district = district,
    subDistrict = subDistrict,
    street = street,
    building = building,
    customAddressString = customAddressString,
    location = if (!hasLat() || !hasLon()) null else Location(lat, lon)
)

fun FlatAdOuterClass.FlatInfo.toDomain(): FlatInfo = FlatInfo(
    floor = floor,
    totalFloors = totalFloors,
    spaceSquareMeters = spaceSquareMeters,
    rooms = rooms,
    bedrooms = bedrooms,
    buildingType = safeValueOf<FlatInfo.BuildingType>(buildingType),
    flatType = safeValueOf<FlatInfo.FlatType>(flatType)
)

fun FlatAdOuterClass.Contacts.toDomain(): Contacts = Contacts(
    flatServiceLink = flatServiceLink,
    phoneNumber = phoneNumber,
    messengerIds = messengerIdsMap.mapKeys { Contacts.Messenger.valueOf(it.key) }
)

fun FlatAdOuterClass.UserInfo.toDomain(): UserInfo = UserInfo(
    id = id,
    name = name,
    locale = locale,
    searchEnabled = searchEnabled,
)

private fun <T> nullSafeSet(from: T?, to: Consumer<T>) {
    from?.let { to.accept(it) }
}

private fun nullSafeSetEnum(from: Enum<*>?, to: Consumer<String>) {
    from?.let { to.accept(it.name) }
}

inline fun <reified T : Enum<T>> safeValueOf(type: String): T? {
    return try {
        java.lang.Enum.valueOf(T::class.java, type)
    } catch (e: IllegalArgumentException) {
        null
    }
}

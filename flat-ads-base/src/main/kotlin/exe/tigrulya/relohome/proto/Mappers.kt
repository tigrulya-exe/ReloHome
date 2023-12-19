package exe.tigrulya.relohome.proto

import exe.tigrulya.relohome.api.FlatAdOuterClass
import exe.tigrulya.relohome.model.Address
import exe.tigrulya.relohome.model.Contacts
import exe.tigrulya.relohome.model.FlatAd
import exe.tigrulya.relohome.model.FlatInfo
import kotlin.reflect.KMutableProperty0

fun FlatAd.toProto(): FlatAdOuterClass.FlatAd = FlatAdOuterClass.FlatAd.newBuilder()
    .also { proto ->
        proto.id = id
        proto.title = title
        proto.address = address.toProto()
        proto.info = info.toProto()
        nullSafeSet(price?.amount, proto::priceAmount)
        nullSafeSetEnum(price?.currency, proto::priceCurrency)
        nullSafeSet(description, proto::description)
        proto.contacts = contacts.toProto()
        proto.serviceId = serviceId
        proto.addAllImages(images.map { it.url })
    }.build()


fun Address.toProto(): FlatAdOuterClass.Address = FlatAdOuterClass.Address.newBuilder()
    .also { proto ->
        proto.cityName = city.name
        proto.cityCountry = city.country
        nullSafeSet(district, proto::district)
        nullSafeSet(subDistrict, proto::subDistrict)
        nullSafeSet(street, proto::street)
        nullSafeSet(building, proto::building)
        nullSafeSet(customAddressString, proto::customAddressString)
        nullSafeSet(location?.lat, proto::lat)
        nullSafeSet(location?.lon, proto::lat)
    }.build()

fun FlatInfo.toProto(): FlatAdOuterClass.FlatInfo = FlatAdOuterClass.FlatInfo.newBuilder()
    .also { proto ->
        proto.floor = floor
        nullSafeSet(totalFloors, proto::totalFloors)
        nullSafeSet(spaceSquareMeters, proto::spaceSquareMeters)
        nullSafeSet(rooms, proto::rooms)
        nullSafeSet(bedrooms, proto::bedrooms)
        nullSafeSetEnum(buildingType, proto::buildingType)
        nullSafeSetEnum(flatType, proto::flatType)
    }.build()

fun Contacts.toProto(): FlatAdOuterClass.Contacts = FlatAdOuterClass.Contacts.newBuilder()
    .also { proto ->
        proto.flatServiceLink = flatServiceLink
        nullSafeSet(phoneNumber, proto::phoneNumber)
        proto.putAllMessengerIds(messengerIds.mapKeys { it.key.name })
    }.build()

private fun <T> nullSafeSet(from: T?, to: KMutableProperty0<T>) {
    from?.let { to.set(it) }
}

private fun nullSafeSetEnum(from: Enum<*>?, to: KMutableProperty0<String>) {
    from?.let { to.set(it.name) }
}


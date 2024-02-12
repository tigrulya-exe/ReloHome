package exe.tigrulya.relohome.handler.server

import exe.tigrulya.relohome.api.UserHandlerGatewayGrpcKt
import exe.tigrulya.relohome.api.UserHandlerGatewayOuterClass
import exe.tigrulya.relohome.api.grpc.GrpcServer
import exe.tigrulya.relohome.handler.service.UserService
import exe.tigrulya.relohome.model.City
import exe.tigrulya.relohome.model.NumRange
import exe.tigrulya.relohome.model.UserCreateDto
import exe.tigrulya.relohome.model.UserSearchOptionsDto

class FlatAdsHandlerGrpcServer(port: Int, userService: UserService = UserService()) :
    GrpcServer("FlatAdsHandler", port, FlatAdsHandlerGrpcService(userService))

class FlatAdsHandlerGrpcService(
    private val userService: UserService
) : UserHandlerGatewayGrpcKt.UserHandlerGatewayCoroutineImplBase() {
    override suspend fun registerUser(
        request: UserHandlerGatewayOuterClass.UserCreateRequest
    ): UserHandlerGatewayOuterClass.Empty {
        userService.registerUser(
            UserCreateDto(
                name = request.name,
                externalId = request.externalId
            )
        )
        return UserHandlerGatewayOuterClass.Empty.getDefaultInstance()
    }

    override suspend fun setLocation(
        request: UserHandlerGatewayOuterClass.SetLocationRequest
    ): UserHandlerGatewayOuterClass.Empty {
        userService.setLocation(
            externalId = request.externalId,
            city = City(
                name = request.city,
                country = request.country
            )
        )
        return UserHandlerGatewayOuterClass.Empty.getDefaultInstance()
    }

    override suspend fun setSearchOptions(
        request: UserHandlerGatewayOuterClass.SetSearchOptionsRequest
    ): UserHandlerGatewayOuterClass.Empty {
        userService.setSearchOptions(
            externalUserId = request.externalId,
            searchOptions = UserSearchOptionsDto(
                priceRange = request.priceRange.toDomainNumRange(),
                roomRange = request.roomRange.toDomainNumRange(),
                subDistricts = request.subDistrictsList
            )
        )
        return UserHandlerGatewayOuterClass.Empty.getDefaultInstance()
    }

    override suspend fun toggleSearch(
        request: UserHandlerGatewayOuterClass.ToggleSearchRequest
    ): UserHandlerGatewayOuterClass.ToggleSearchResponse {
        return UserHandlerGatewayOuterClass.ToggleSearchResponse
            .newBuilder().apply {
                searchEnabled = userService.toggleSearch(request.externalId)
            }.build()
    }

    private fun UserHandlerGatewayOuterClass.NumRange.toDomainNumRange(): NumRange = NumRange(
        if (from < 0) null else from,
        if (to < 0) null else to,
    )
}
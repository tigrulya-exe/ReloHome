package exe.tigrulya.relohome.handler.server

import exe.tigrulya.relohome.api.UserHandlerGatewayGrpcKt
import exe.tigrulya.relohome.api.UserHandlerGatewayOuterClass
import exe.tigrulya.relohome.api.grpc.GrpcServer
import exe.tigrulya.relohome.error.WithGrpcServerErrorHandling
import exe.tigrulya.relohome.handler.service.UserService
import exe.tigrulya.relohome.model.City
import exe.tigrulya.relohome.model.NumRange
import exe.tigrulya.relohome.model.UserCreateDto
import exe.tigrulya.relohome.model.UserSearchOptionsDto

class FlatAdsHandlerGrpcServer(port: Int, userService: UserService = UserService()) :
    GrpcServer("FlatAdsHandler", port, FlatAdsHandlerGrpcService(userService))

class FlatAdsHandlerGrpcService(
    private val userService: UserService
) : UserHandlerGatewayGrpcKt.UserHandlerGatewayCoroutineImplBase(), WithGrpcServerErrorHandling {
    override suspend fun registerUser(
        request: UserHandlerGatewayOuterClass.UserCreateRequest
    ): UserHandlerGatewayOuterClass.Empty = withErrorHandling("registerUser") {
        userService.registerUser(
            UserCreateDto(
                name = request.name,
                externalId = request.externalId
            )
        )
        UserHandlerGatewayOuterClass.Empty.getDefaultInstance()
    }

    override suspend fun setLocation(
        request: UserHandlerGatewayOuterClass.SetLocationRequest
    ): UserHandlerGatewayOuterClass.Empty = withErrorHandling("setLocation") {
        userService.setLocation(
            externalId = request.externalId,
            city = City(
                name = request.city,
                country = request.country
            )
        )
        UserHandlerGatewayOuterClass.Empty.getDefaultInstance()
    }

    override suspend fun setSearchOptions(
        request: UserHandlerGatewayOuterClass.SetSearchOptionsRequest
    ): UserHandlerGatewayOuterClass.Empty = withErrorHandling("setSearchOptions") {
        userService.setSearchOptions(
            externalUserId = request.externalId,
            searchOptions = UserSearchOptionsDto(
                priceRange = request.priceRange.toDomainNumRange(),
                roomRange = request.roomRange.toDomainNumRange(),
                areaRange = request.areaRange.toDomainNumRange(),
                bedroomRange = request.bedroomRange.toDomainNumRange(),
                floorRange = request.floorRange.toDomainNumRange(),
                subDistricts = request.subDistrictsList
            )
        )
        UserHandlerGatewayOuterClass.Empty.getDefaultInstance()
    }

    override suspend fun toggleSearch(
        request: UserHandlerGatewayOuterClass.ToggleSearchRequest
    ): UserHandlerGatewayOuterClass.ToggleSearchResponse = withErrorHandling("toggleSearch") {
        UserHandlerGatewayOuterClass.ToggleSearchResponse
            .newBuilder().apply {
                searchEnabled = userService.toggleSearch(request.externalId)
            }.build()
    }

    private fun UserHandlerGatewayOuterClass.NumRange.toDomainNumRange(): NumRange = NumRange(
        if (from < 0) null else from,
        if (to < 0) null else to,
    )
}
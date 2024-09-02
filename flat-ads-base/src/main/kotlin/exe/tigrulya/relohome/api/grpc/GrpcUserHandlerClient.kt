package exe.tigrulya.relohome.api.grpc

import exe.tigrulya.relohome.api.user_handler.UserHandlerGateway
import exe.tigrulya.relohome.api.UserHandlerGatewayGrpcKt
import exe.tigrulya.relohome.api.UserHandlerGatewayOuterClass
import exe.tigrulya.relohome.error.WithGrpcClientErrorHandling
import exe.tigrulya.relohome.model.City
import exe.tigrulya.relohome.model.NumRange
import exe.tigrulya.relohome.model.UserCreateDto
import exe.tigrulya.relohome.model.UserSearchOptionsDto
import io.grpc.ManagedChannelBuilder

open class GrpcUserHandlerClient(serverUrl: String) : UserHandlerGateway, WithGrpcClientErrorHandling {

    private val grpcClient: UserHandlerGatewayGrpcKt.UserHandlerGatewayCoroutineStub

    init {
        val channel = ManagedChannelBuilder.forTarget(serverUrl).usePlaintext().build()
        grpcClient = UserHandlerGatewayGrpcKt.UserHandlerGatewayCoroutineStub(channel)
    }

    override suspend fun registerUser(user: UserCreateDto) = withErrorHandling {
        val request = UserHandlerGatewayOuterClass.UserCreateRequest.newBuilder()
            .apply {
                this.externalId = user.externalId
                this.name = user.name
            }.build()
        grpcClient.registerUser(request)
    }

    override suspend fun setLocation(externalId: String, city: City) = withErrorHandling {
        val request = UserHandlerGatewayOuterClass.SetLocationRequest.newBuilder()
            .apply {
                this.externalId = externalId
                this.city = city.name
                this.country = city.country
            }.build()
        grpcClient.setLocation(request)
    }

    override suspend fun setSearchOptions(
        externalUserId: String,
        searchOptions: UserSearchOptionsDto
    ) = withErrorHandling {
        val request = UserHandlerGatewayOuterClass.SetSearchOptionsRequest
            .newBuilder().apply {
                externalId = externalUserId
                priceRange = toGrpcNumRange(searchOptions.priceRange)
                roomRange = toGrpcNumRange(searchOptions.roomRange)
                areaRange = toGrpcNumRange(searchOptions.areaRange)
                bedroomRange = toGrpcNumRange(searchOptions.bedroomRange)
                floorRange = toGrpcNumRange(searchOptions.floorRange)
                addAllSubDistricts(searchOptions.subDistricts)
            }.build()

        grpcClient.setSearchOptions(request)
    }

    override suspend fun toggleSearch(externalId: String): Boolean {
        val result = withSilentErrorHandling {
            val request = UserHandlerGatewayOuterClass.ToggleSearchRequest
                .newBuilder().apply {
                    this.externalId = externalId
                }.build()

            grpcClient.toggleSearch(request).searchEnabled
        }

        return result.getOrThrow()
    }

    private fun toGrpcNumRange(range: NumRange) = UserHandlerGatewayOuterClass.NumRange.newBuilder().apply {
        from = range.from ?: -1
        to = range.to ?: -1
    }.build()
}
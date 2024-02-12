package exe.tigrulya.relohome.api.grpc

import exe.tigrulya.relohome.api.UserHandlerGateway
import exe.tigrulya.relohome.api.UserHandlerGatewayGrpcKt
import exe.tigrulya.relohome.api.UserHandlerGatewayOuterClass
import exe.tigrulya.relohome.model.City
import exe.tigrulya.relohome.model.NumRange
import exe.tigrulya.relohome.model.UserCreateDto
import exe.tigrulya.relohome.model.UserSearchOptionsDto
import io.grpc.ManagedChannelBuilder

class GrpcUserHandlerClient(serverUrl: String) : UserHandlerGateway {

    private val grpcClient: UserHandlerGatewayGrpcKt.UserHandlerGatewayCoroutineStub

    init {
        val channel = ManagedChannelBuilder.forTarget(serverUrl).usePlaintext().build()
        grpcClient = UserHandlerGatewayGrpcKt.UserHandlerGatewayCoroutineStub(channel)
    }

    // todo mb turn UserHandlerGateway to suspend api
    override suspend fun registerUser(user: UserCreateDto) {
        val request = UserHandlerGatewayOuterClass.UserCreateRequest.newBuilder()
            .apply {
                this.externalId = user.externalId
                this.name = user.name
            }.build()
        grpcClient.registerUser(request)
    }

    override suspend fun setLocation(externalId: String, city: City) {
        val request = UserHandlerGatewayOuterClass.SetLocationRequest.newBuilder()
            .apply {
                this.externalId = externalId
                this.city = city.name
                this.country = city.country
            }.build()
        grpcClient.setLocation(request)
    }

    override suspend fun setSearchOptions(externalId: String, searchOptions: UserSearchOptionsDto) {
        val request = UserHandlerGatewayOuterClass.SetSearchOptionsRequest
            .newBuilder().apply {
                this.externalId = externalId
                this.priceRange = toGrpcNumRange(searchOptions.priceRange)
                this.roomRange = toGrpcNumRange(searchOptions.roomRange)
                addAllSubDistricts(searchOptions.subDistricts)
            }.build()

        grpcClient.setSearchOptions(request)
    }

    override suspend fun toggleSearch(externalId: String): Boolean {
        val request = UserHandlerGatewayOuterClass.ToggleSearchRequest
            .newBuilder().apply {
                this.externalId = externalId
            }.build()

        return grpcClient.toggleSearch(request).searchEnabled
    }

    private fun toGrpcNumRange(range: NumRange) = UserHandlerGatewayOuterClass.NumRange.newBuilder().apply {
        from = range.from ?: -1
        to = range.to ?: -1
    }.build()
}
package exe.tigrulya.relohome.api.grpc

import exe.tigrulya.relohome.api.UserHandlerGatewayGrpcKt
import exe.tigrulya.relohome.api.UserHandlerGatewayOuterClass
import exe.tigrulya.relohome.api.user_handler.UserHandlerGateway
import exe.tigrulya.relohome.error.WithGrpcClientErrorHandling
import exe.tigrulya.relohome.model.*
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
                this.locale = user.locale
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

    override suspend fun setLocale(externalId: String, locale: String) {
        val request = UserHandlerGatewayOuterClass.SetLocaleRequest.newBuilder()
            .apply {
                this.externalId = externalId
                this.locale = locale
            }.build()
        grpcClient.setLocale(request)
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

    override suspend fun toggleSearch(externalId: String): Boolean = withSilentErrorHandling {
        val request = UserHandlerGatewayOuterClass.ToggleSearchRequest
            .newBuilder().apply {
                this.externalId = externalId
            }.build()

        grpcClient.toggleSearch(request).searchEnabled
    }.getOrThrow()

    override suspend fun getUserInfo(externalId: String): UserInfo = withSilentErrorHandling {
        val request = UserHandlerGatewayOuterClass.GetUserInfoRequest
            .newBuilder().apply {
                this.externalId = externalId
            }.build()

        grpcClient.getUserInfo(request).run {
            UserInfo(
                id = id,
                name = name,
                locale = locale,
                searchEnabled = searchEnabled
            )
        }
    }.getOrThrow()

    private fun toGrpcNumRange(range: NumRange) = UserHandlerGatewayOuterClass.NumRange.newBuilder().apply {
        from = range.from ?: -1
        to = range.to ?: -1
    }.build()
}
package exe.tigrulya.relohome.api.grpc

import exe.tigrulya.relohome.util.LoggerProperty
import io.grpc.BindableService
import io.grpc.ServerBuilder

open class GrpcServer(
    private val serverName: String,
    private val port: Int,
    vararg services: BindableService) {

    private val grpcServer = ServerBuilder.forPort(port)
        .apply {
            services.forEach(this::addService)
        }
        .build()

    private val logger by LoggerProperty()

    fun start() {
        logger.info("Starting $serverName GRPC Server on port $port")
        grpcServer.start()
        logger.info("$serverName GRPC Server started")
    }
}

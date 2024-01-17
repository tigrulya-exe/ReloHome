plugins {
    id("com.google.protobuf") version ("0.9.4")
}

val protobufVersion = "3.25.1"
val grpcVersionKotlin = "1.4.1"
val grpcVersionJava = "1.59.0"

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersionJava"
        }
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcVersionKotlin:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
                create("grpckt")
            }
            it.builtins {
                create("kotlin")
            }
        }
    }
}

dependencies {
    api("com.google.protobuf:protobuf-kotlin:$protobufVersion")
    api("io.grpc:grpc-protobuf:$grpcVersionJava")
    api("io.grpc:grpc-stub:$grpcVersionJava")
    api("io.grpc:grpc-kotlin-stub:$grpcVersionKotlin")
    implementation("io.grpc:grpc-netty:$grpcVersionJava")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.+")
}
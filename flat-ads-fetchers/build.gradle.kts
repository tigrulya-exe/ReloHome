val ktorVersion = "2.2.2"
val logbackVersion = "1.2.10"

subprojects {
    dependencies {
        implementation("io.ktor:ktor-client-core:$ktorVersion")
        implementation("io.ktor:ktor-client-cio:$ktorVersion")
        implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
        implementation("io.ktor:ktor-serialization-gson:$ktorVersion")
        implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
        implementation("io.ktor:ktor-client-logging:$ktorVersion")
        implementation("ch.qos.logback:logback-classic:$logbackVersion")
    }
}


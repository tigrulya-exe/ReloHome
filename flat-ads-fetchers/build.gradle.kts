val ktorVersion by properties
val logbackVersion = "1.2.10"

subprojects {
    apply(plugin = "io.ktor.plugin")

    dependencies {
        implementation(project(":flat-ads-base"))
        implementation("io.ktor:ktor-client-core")
        implementation("io.ktor:ktor-client-cio")
        implementation("io.ktor:ktor-client-content-negotiation")
        implementation("io.ktor:ktor-serialization-gson")
        implementation("io.ktor:ktor-serialization-jackson")
        implementation("io.ktor:ktor-client-logging")
        implementation("ch.qos.logback:logback-classic:$logbackVersion")
    }
}


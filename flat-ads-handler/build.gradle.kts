val exposedVersion by properties
val ktorVersion by properties
val logbackVersion by properties

dependencies {
    implementation(project(":flat-ads-base"))

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-mustache-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-jackson-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-network-tls-certificates")
    implementation("io.ktor:ktor-server-config-yaml")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.liquibase:liquibase-core:4.23.2")
    implementation("io.ktor:ktor-server-call-logging-jvm:2.3.6")
    runtimeOnly("com.mattbertolini:liquibase-slf4j:5.0.0")

    implementation("org.postgresql:postgresql:42.7.0")
}

tasks.named<Jar>("fatJar") {
    manifest.attributes["Main-Class"] = "exe.tigrulya.relohome.handler.Application"
}
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val exposedVersion by properties
val ktorVersion by properties
val logbackVersion by properties

dependencies {
    implementation(project(":flat-ads-base"))

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    implementation("io.lettuce:lettuce-core:6.3.2.RELEASE")

    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-mustache-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-jackson-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-network-tls-certificates")
    implementation("io.ktor:ktor-server-config-yaml")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-server-call-logging-jvm:2.3.6")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.liquibase:liquibase-core:4.23.2")
    runtimeOnly("com.mattbertolini:liquibase-slf4j:5.0.0")

    implementation("org.postgresql:postgresql:42.7.0")
}

application {
    mainClass.set("exe.tigrulya.relohome.handler.ApplicationKt")
}

tasks.withType<ShadowJar> {
    enabled = true
}

tasks.withType<DockerBuildImage> {
    enabled = true
}

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
}

val telegramBotsVersion = "6.8.0"
val exposedVersion = "0.40.1"
val ktorVersion by properties
val logbackVersion by properties

dependencies {
    implementation(project(":flat-ads-base"))
    implementation(project(":flat-ads-handler"))
    implementation(project(":flat-ads-fetchers:fetcher-ssge"))
    implementation(project(":flat-ads-fetchers:fetcher-base"))
    implementation(project(":flat-ads-notifier:notifier-telegram"))

    implementation("org.telegram:telegrambots:$telegramBotsVersion")
    implementation("org.telegram:telegrambots-abilities:$telegramBotsVersion")

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
    implementation("io.ktor:ktor-server-call-logging-jvm:2.3.6")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.liquibase:liquibase-core:4.23.2")
    runtimeOnly("com.mattbertolini:liquibase-slf4j:5.0.0")

    implementation("org.xerial:sqlite-jdbc:3.42.0.0")
}

application {
    mainClass.set("exe.tigrulya.relohome.monolith.MainKt")
}

tasks.withType<ShadowJar> {
    exclude("fetcher-ssge.yaml")
    exclude("notifier-tg.yaml")
    exclude("application.yaml")
    exclude("keystore.jks")

    enabled = true
}

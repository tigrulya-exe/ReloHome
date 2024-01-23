plugins {
    application
}

val telegramBotsVersion = "6.8.0"
val exposedVersion = "0.40.1"

application {
    mainClass.set("exe.tigrulya.relohome.demo.MainKt")
}

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

    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.liquibase:liquibase-core:4.23.2")
    runtimeOnly("com.mattbertolini:liquibase-slf4j:5.0.0")

    implementation("org.xerial:sqlite-jdbc:3.42.0.0")
}
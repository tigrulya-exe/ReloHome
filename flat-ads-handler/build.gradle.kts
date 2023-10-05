plugins {
    application
}

val exposedVersion = "0.40.1"

dependencies {
    implementation(project(":flat-ads-fetchers:fetcher-model"))

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.liquibase:liquibase-core:4.23.2")
    runtimeOnly("com.mattbertolini:liquibase-slf4j:5.0.0")

    implementation("org.xerial:sqlite-jdbc:3.42.0.0")
}
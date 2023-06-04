plugins {
    application
}

val exposedVersion = "0.40.1"

dependencies {
    implementation(project(":flat-ads-fetchers:fetcher-model"))

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    implementation("org.xerial:sqlite-jdbc:3.42.0.0")
}
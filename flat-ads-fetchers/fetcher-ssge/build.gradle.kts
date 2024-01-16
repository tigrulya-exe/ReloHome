plugins {
    application
}

dependencies {
    implementation(project(":flat-ads-fetchers:fetcher-base"))
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
}

application {
    mainClass.set("exe.tigrulya.relohome.ssge")
}
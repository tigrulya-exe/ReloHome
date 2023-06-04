plugins {
    application
}

dependencies {
    implementation(project(":flat-ads-fetchers:fetcher-base"))

    implementation("org.jsoup:jsoup:1.15.4")
}

application {
    mainClass.set("exe.tigrulya.relohome.connector.listam.MainKt")
}

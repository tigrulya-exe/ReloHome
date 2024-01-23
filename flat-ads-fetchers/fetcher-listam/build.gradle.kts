plugins {
    application
}

dependencies {
    implementation(project(":flat-ads-fetchers:fetcher-base"))

    implementation("org.jsoup:jsoup:1.15.4")
}

application {
    mainClass.set("exe.tigrulya.relohome.listam.ListAmFetcherKt")
}

tasks.named<Jar>("fatJar") {
    // TODO
    manifest.attributes["Main-Class"] = "exe.tigrulya.relohome.listam.ListAmFetcherKt"
}
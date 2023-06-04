dependencies {
    api(project(":flat-ads-fetchers:fetcher-model"))
    // TODO: tmp move gateway to separate module
    api(project(":flat-ads-handler"))

    implementation("org.jsoup:jsoup:1.15.4")
}

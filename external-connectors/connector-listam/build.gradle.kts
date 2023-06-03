plugins {
    application
}

dependencies {
    implementation("org.jsoup:jsoup:1.15.4")
    implementation(project(":external-connectors:connector-base"))
}

application {
    mainClass.set("exe.tigrulya.MainKt")
}

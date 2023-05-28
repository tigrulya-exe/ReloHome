plugins {
    application
}

val kotestVersion = "5.6.2"
dependencies {
    implementation("org.jsoup:jsoup:1.15.4")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
}

application {
    mainClass.set("exe.tigrulya.MainKt")
}

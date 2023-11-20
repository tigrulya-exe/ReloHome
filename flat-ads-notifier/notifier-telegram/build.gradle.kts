plugins {
    application
}

val telegramBotsVersion = "6.8.0"

dependencies {
    implementation(project(":flat-ads-base"))

    implementation("org.telegram:telegrambots:$telegramBotsVersion")
    implementation("org.telegram:telegrambots-abilities:$telegramBotsVersion")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.+")
    // just for rate limiter :(
    implementation("com.google.guava:guava:32.1.3-jre")
}

application {
    mainClass.set("exe.tigrulya.relohome.connector.listam.MainKt")
}

// TODO move to base
tasks.register<Jar>("fatJar") {
    group = "build"
    manifest.attributes["Main-Class"] = "exe.tigrulya.relohome.notifier.telegram.Main"
    archiveBaseName.set("${project.name}-fat")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map {
            zipTree(it).matching {
                exclude(
                    listOf(
                        "META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA"
                    )
                )
            }
        }

    from(dependencies)
    with(tasks.jar.get())
}
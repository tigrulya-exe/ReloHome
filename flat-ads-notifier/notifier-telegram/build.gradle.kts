plugins {
    application
}

val telegramBotsVersion = "6.0.1"

dependencies {
    implementation("org.telegram:telegrambots:$telegramBotsVersion")
    implementation("org.telegram:telegrambots-abilities:$telegramBotsVersion")
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
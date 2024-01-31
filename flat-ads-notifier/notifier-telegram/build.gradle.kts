import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage

plugins {
    application
    id("com.bmuschko.docker-remote-api")
}

val telegramBotsVersion = "6.8.0"

dependencies {
    implementation(project(":flat-ads-base"))
    implementation(project(":flat-ads-notifier:notifier-base"))

    implementation("org.telegram:telegrambots:$telegramBotsVersion")
    implementation("org.telegram:telegrambots-abilities:$telegramBotsVersion")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.+")
    // just for rate limiter :(
    implementation("com.google.guava:guava:32.1.3-jre")
}

tasks.named<Jar>("fatJar") {
    manifest.attributes["Main-Class"] = "exe.tigrulya.relohome.notifier.telegram.MainKt"
}

tasks.register<DockerBuildImage>("buildDockerImage") {
    dependsOn("fatJar")
    inputDir.set(file("."))
    images.add("relohome/${project.name}:${project.version}")
}

application {
    mainClass.set("exe.tigrulya.relohome.notifier.telegram.MainKt")
}
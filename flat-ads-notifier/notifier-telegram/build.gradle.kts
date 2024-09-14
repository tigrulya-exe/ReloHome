import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
}

val telegramBotsVersion = "6.8.0"
val ktTelegramBotsVersion = "6.8.0"

dependencies {
    implementation(project(":flat-ads-base"))
    implementation(project(":flat-ads-notifier:notifier-base"))

    implementation("org.telegram:telegrambots:$telegramBotsVersion")
    implementation("org.telegram:telegrambots-abilities:$telegramBotsVersion")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.+")
    // just for rate limiter :(
    implementation("com.google.guava:guava:32.1.3-jre")

    implementation("dev.inmo:tgbotapi:18.0.0")
    implementation("io.lettuce:lettuce-core:6.3.2.RELEASE")
}

application {
    mainClass.set("exe.tigrulya.relohome.notifier.telegram.MainKt")
}

tasks.withType<ShadowJar> {
    enabled = true
}

tasks.withType<DockerBuildImage> {
    enabled = true
}
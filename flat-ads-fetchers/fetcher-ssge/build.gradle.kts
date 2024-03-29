import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    implementation(project(":flat-ads-fetchers:fetcher-base"))
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
}

application {
    mainClass.set("exe.tigrulya.relohome.ssge.MainKt")
}

tasks.withType<ShadowJar> {
    enabled = true
}

tasks.withType<DockerBuildImage> {
    enabled = true
}
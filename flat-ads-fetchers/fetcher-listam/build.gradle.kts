import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage

plugins {
    application
    id("com.bmuschko.docker-remote-api")
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

tasks.register<DockerBuildImage>("buildDockerImage") {
    dependsOn("fatJar")
    inputDir.set(file("."))
    images.add("relohome/${project.name}:${project.version}")
}
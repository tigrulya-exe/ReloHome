import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar


dependencies {
    implementation(project(":flat-ads-fetchers:fetcher-base"))

    implementation("org.jsoup:jsoup:1.15.4")
}

application {
    mainClass.set("exe.tigrulya.relohome.listam.ListAmFetcherKt")
}

tasks.withType<ShadowJar> {
    enabled = true
}

tasks.withType<DockerBuildImage> {
    enabled = true
}
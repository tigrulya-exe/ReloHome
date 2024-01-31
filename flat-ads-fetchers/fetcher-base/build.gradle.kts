import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    api("org.jsoup:jsoup:1.15.4")
}

tasks.named<ShadowJar>("shadowJar") {
    enabled = false
}
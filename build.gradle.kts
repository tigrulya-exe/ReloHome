import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.20"
    id("io.ktor.plugin") version "2.3.6"
}

allprojects {
    group = "exe.tigrulya"
    version = "0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "io.ktor.plugin")

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    val kotestVersion = "5.6.2"
    dependencies {
        implementation("org.slf4j:slf4j-api:2.0.9")
        implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.21.1")
        implementation("org.apache.logging.log4j:log4j-core:2.21.1")

        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

        testImplementation(kotlin("test"))
        testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
        testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
        testImplementation("io.kotest:kotest-property:$kotestVersion")
    }

    tasks.test {
        useJUnitPlatform()
    }
}




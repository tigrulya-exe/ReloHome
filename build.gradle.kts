import groovyjarjarantlr4.v4.codegen.target.JavaTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.20"
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


    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    val kotestVersion = "5.6.2"
    dependencies {
        testImplementation(kotlin("test"))
        testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
        testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
        testImplementation("io.kotest:kotest-property:$kotestVersion")
    }

    tasks.test {
        useJUnitPlatform()
    }
}




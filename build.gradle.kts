import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.21"
    id("org.liquibase.gradle") version "2.2.1"
    id("io.ktor.plugin") version "2.3.6" apply false
    id("com.bmuschko.docker-remote-api") version "9.4.0" apply false
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

allprojects {
    group = "exe.tigrulya"
    version = "0.3.1"

    repositories {
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }

    // just for dev purposes: gen tables by JB Exposed and extract migration xmls using gradle liquibase plugin
    apply(plugin = "org.liquibase.gradle")
    dependencies {
        liquibaseRuntime("org.liquibase:liquibase-core:4.16.1")
        liquibaseRuntime("info.picocli:picocli:4.6.1")
        liquibaseRuntime("javax.xml.bind:jaxb-api:2.3.1")
        liquibaseRuntime("org.liquibase:liquibase-groovy-dsl:2.0.1")
        liquibaseRuntime("ch.qos.logback:logback-core:1.2.3")
        liquibaseRuntime("ch.qos.logback:logback-classic:1.2.3")
        liquibaseRuntime("org.postgresql:postgresql:42.7.0")
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "com.bmuschko.docker-remote-api")
    apply(plugin = "io.ktor.plugin")
    apply(plugin = "com.github.johnrengelman.shadow")

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    val kotestVersion = "5.6.2"
    val kafkaVersion by properties

    dependencies {
        implementation("org.slf4j:slf4j-api:2.0.9")
        implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.21.1")
        implementation("org.apache.logging.log4j:log4j-core:2.21.1")

        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
        runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.7.3")

        implementation("org.apache.kafka:kafka-clients:$kafkaVersion")

        testImplementation(kotlin("test"))
        testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
        testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
        testImplementation("io.kotest:kotest-property:$kotestVersion")
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.withType<ShadowJar> {
        archiveFileName.set("${project.name}-fat-${project.version}.jar")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        mergeServiceFiles()

        // tasks.processResources.get().enabled = false

        enabled = false
    }

    tasks.register<DockerBuildImage>("buildDockerImage") {
        dependsOn("shadowJar")
        inputDir.set(file("."))
        images.add("relohome/${project.name}:${project.version}")

        enabled = false
    }
}

liquibase {
    activities.register("dev") {
        this.arguments = mapOf(
            "changeLogFile" to "./dbchangelog.xml",
            "url" to "jdbc:postgresql://localhost:65432/ReloHome?user=root&password=toor",
            "diffTypes" to "tables,views,columns,indexes,foreignkeys,primarykeys,uniqueconstraints"
        )
    }
}


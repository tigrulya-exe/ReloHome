import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.20"
    id("io.ktor.plugin") version "2.3.6"
    id("org.liquibase.gradle") version "2.2.1"
}

allprojects {
    group = "exe.tigrulya"
    version = "0.1-SNAPSHOT"

    repositories {
        mavenCentral()
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

liquibase {
    activities.register("dev") {
        this.arguments = mapOf(
            "changeLogFile" to "./dbchangelog.xml",
            "url" to "jdbc:postgresql://localhost:65432/ReloHome?user=root&password=toor",
            "diffTypes" to "tables,views,columns,indexes,foreignkeys,primarykeys,uniqueconstraints"
        )
    }
}


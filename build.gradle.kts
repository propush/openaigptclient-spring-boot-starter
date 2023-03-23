import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream

plugins {
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.spring") version "1.8.10"
    `maven-publish`
}

fun composeBranchName(): String? =
    try {
        println("Task Getting Branch Name...")
        val stdout = ByteArrayOutputStream()
        exec {
            commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
            standardOutput = stdout
        }
        stdout.toString().trim()
    } catch (e: Exception) {
        println("Exception = " + e.message)
        null
    }

fun getVersionPostfix(): String {
    val branch = composeBranchName() ?: throw IllegalArgumentException("Branch name not defined")
    println("Git Current Branch = $branch")
    return when (branch) {
        "master" -> "SNAPSHOT"
        "release" -> "RELEASE"
        else -> branch.toUpperCase()
    }
}
group = "com.pushkin"
val postfix = getVersionPostfix()
version = "1.0.13-$postfix"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
}

tasks {

    bootJar {
        enabled = false
    }

    jar {
        enabled = true
        archiveClassifier.set("")
    }

    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }
    withType<Test> {
        useJUnitPlatform()
        testLogging { events("passed", "skipped", "failed", "standardOut", "standardError") }
        systemProperties = System.getProperties().map { it.key.toString() to it.value.toString() }.toMap()
    }
}

plugins {
    kotlin("jvm") version "1.5.30"
    kotlin("plugin.serialization") version "1.5.30"
    id("application")
}

group = "com.soarex16"
version = "1.0"

application {
    mainClass.set("com.soarex16.iexporer.cli.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    // https://spoon.gforge.inria.fr/
    implementation("fr.inria.gforge.spoon:spoon-core:9.1.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")

    implementation("org.slf4j", "slf4j-simple", "1.7.30")
    implementation("io.github.microutils:kotlin-logging:2.0.11")

    implementation("com.github.ajalt.clikt", "clikt", "3.2.0")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}
plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
}

group = "de.haw.landshut"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(libs.hivemq.mqtt.client)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.tinylog.impl)
    implementation(libs.tinylog.api)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.engine)
    testImplementation(libs.junit.launcher)
    testImplementation(libs.assertj)

    runtimeOnly(libs.jul.tinylog)
    runtimeOnly(libs.slf4j.tinylog)
}

kotlin {
    jvmToolchain(25)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks.test {
    useJUnitPlatform()
}
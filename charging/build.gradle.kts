plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
    application
    java
}

group = "de.weberhome"
version = "0.6.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.gson)
    implementation(libs.j2mod)
    implementation(libs.okhttp)
    implementation(libs.tinylog.impl)
    implementation(libs.tinylog.api)

    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.engine)
    testImplementation(libs.junit.launcher)
    testImplementation(libs.assertj)

    runtimeOnly(libs.jul.tinylog)
    runtimeOnly(libs.kotlinx.datetime)
    runtimeOnly(libs.slf4j.tinylog)
}

configurations.all {
    exclude(group = "org.slf4j", module = "slf4j-reload4j")
    exclude(group = "ch.qos.reload4j", module = "reload4j")
}

application {
    mainClass.set("ChargingKt")
    applicationDefaultJvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}

kotlin {
    jvmToolchain(25)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

distributions {
    main {
        contents {
            from("charging.service")
            from("deploy.sh")
        }
    }
}

tasks.test {
    useJUnit()
    useJUnitPlatform()
}

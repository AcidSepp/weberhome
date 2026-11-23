plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
    application
}

group = "de.weberhome"
version = "0.2.0"

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

application {
    mainClass.set("ControllerKt")
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
            from("controller.service")
            from("deploy.sh")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
plugins {
    alias(libs.plugins.kotlin)
    application
    java
}

group = "de.weberhome"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation(libs.gson)
    implementation(libs.influxdb.client.java)
    implementation(libs.j2mod)
    implementation(libs.okhttp)
    implementation(libs.tinylog.impl)
    implementation(libs.tinylog.api)
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("MainKt")
}

kotlin {
    jvmToolchain(25)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
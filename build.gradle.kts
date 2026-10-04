plugins {
    alias(libs.plugins.kotlin)
    java
    application
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(25)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
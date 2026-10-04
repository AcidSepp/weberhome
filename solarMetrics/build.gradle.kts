plugins {
    alias(libs.plugins.kotlin)
    application
    java
}

group = "de.weberhome"
version = "0.8.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.gson)
    implementation(libs.influxdb.client.java)
    implementation(libs.j2mod)
    implementation(libs.okhttp)
    implementation(libs.tinylog.impl)
    implementation(libs.tinylog.api)
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

distributions {
    main {
        contents {
            from("solarMetrics.service") {
                filter { it.replace("VERSION_PLACEHOLDER", project.version.toString()) }
            }
            from("deploy.sh") {
                filter { it.replace("VERSION_PLACEHOLDER", project.version.toString()) }
            }
        }
    }
}
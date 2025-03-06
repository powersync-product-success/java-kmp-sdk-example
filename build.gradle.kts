plugins {
    id("java")
    application
    kotlin("jvm")
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.powersync:core:1.0.0-BETA25")
    implementation("com.powersync:connector-supabase:1.0.0-BETA25")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.mongodb:mongodb-driver-sync:4.11.1")
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("org.slf4j:slf4j-simple:2.0.9")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.json:json:20240303")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

configurations {
    runtimeClasspath {
        // This transitive dependency doesn't support Java 8. Dropping the module works...
        exclude("co.touchlab.skie", "configuration-annotations")
    }
}

application {
    mainClass = "com.example.Main"
}
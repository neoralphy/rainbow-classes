plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij.platform") version "2.3.0"
}

group = "com.github.rainbowclasses"
version = "1.0.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        phpstorm("2024.3")
        bundledPlugin("com.jetbrains.php")
    }
}

intellijPlatform {
    pluginConfiguration {
        name = "Rainbow Classes"
        version = "1.0.0"
        ideaVersion {
            sinceBuild = "243"
        }
    }
}

// buildSearchableOptions launches a headless IDE, which fails without a display.
// Skip it during local development; re-enable for release builds.
tasks.named("buildSearchableOptions") {
    enabled = false
}

// Use sourceCompatibility instead of jvmToolchain to avoid toolchain auto-download
// The Gradle daemon runs on the JBR specified in gradle.properties
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

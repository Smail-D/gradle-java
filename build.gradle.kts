plugins {
    `java-gradle-plugin`
    kotlin("jvm") version "1.5.31"
    `maven-publish`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(gradleKotlinDsl())
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.unbroken-dome.gradle-plugins:gradle-testsets-plugin:3.0.1")

    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.12")
}

gradlePlugin {
    plugins {
        register("smaildGradleJava") {
            id = "com.smaild.java"
            implementationClass = "com.smaild.gradle.plugins.JavaPlugin"
        }
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}

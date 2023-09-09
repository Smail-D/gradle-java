plugins {
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.2.1"
 }

repositories {
    gradlePluginPortal()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

gradlePlugin {
    website = "https://github.com/Smail-D/gradle-java"
    vcsUrl = "https://github.com/Smail-D/gradle-java"
    plugins {
        create("gradle-java") {
            id = "io.github.smaild.gradle-java"
            implementationClass = "io.github.smaild.gradle.GradleJavaPlugin"
            displayName = "Gradle Java plugin"
            description = "This plugin add integration test source set and java toolchain configuration"
            tags = listOf("java", "integrationtesting", "toolchain")
            version = version
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

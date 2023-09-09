plugins {
    `java-gradle-plugin`
    `maven-publish`
 }

repositories {
    gradlePluginPortal()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

gradlePlugin {
    vcsUrl = "https://github.com/Smail-D/gradle-java.git"
    plugins {
        create("gradle-java") {
            id = "com.github.smaild.gradle-java"
            implementationClass = "com.github.smaild.gradle.GradleJavaPlugin"
            description = "This plugin add integration test source set and java toolchain configuration"
            tags = listOf("integrationtesting", "toolchain")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

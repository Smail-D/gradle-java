package com.smaild.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.internal.jvm.inspection.JvmVendor
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.internal.DefaultJvmVendorSpec
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.unbrokendome.gradle.plugins.testsets.dsl.testSets

class JavaPlugin : Plugin<Project> {

    companion object {
        const val JAVA_LIBRARY: String = "java-library"
        const val JAVA_VERSION: Int = 11
    }

    override fun apply(project: Project) {
        val isLibrary = project.hasProperty(JAVA_LIBRARY) && project.property(JAVA_LIBRARY).toString() == "true"

        // Plugins
        if (isLibrary) {
            project.plugins.apply("java-library")
        } else {
            project.plugins.apply("java")
        }

        project.plugins.apply("org.unbroken-dome.test-sets")

        // Configurations
        project.configure<JavaPluginExtension> {
            toolchain {
                it.languageVersion.set(JavaLanguageVersion.of(JAVA_VERSION))
                it.vendor.set(DefaultJvmVendorSpec.of(JvmVendor.KnownJvmVendor.ADOPTOPENJDK))
            }
        }

        if (isLibrary) {
           project.configure<JavaPluginExtension> {
               withSourcesJar()
               withJavadocJar()
           }
        }

        // Setup Integration tests folder
        if (project.file("${project.projectDir}/src/integration").exists()) {
            project.testSets {
                "integration" {
                    dirName = "integration"
                }
            }

            project.tasks.getByName("integration") {
                it.mustRunAfter("test")
            }

            project.tasks.getByName("check") {
                it.dependsOn("integration")
            }
        }

        // Setup Junit
        project.tasks.withType<Test> {
            useJUnitPlatform {
                it.includeEngines("junit-jupiter")
            }
        }
    }

}
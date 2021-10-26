package com.smaild.gradle.plugins

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.*

class JavaPluginTest {

    @get:Rule
    val projectDir = TemporaryFolder()

    private lateinit var buildFile: File
    private lateinit var propertiesFile: File

    @Before
    fun setUp() {
        buildFile = projectDir.newFile("build.gradle")
        propertiesFile = projectDir.newFile("gradle.properties")
    }

    @Test
    fun shouldBuildProject() {
        val buildFileContent = """
            plugins {
                id "com.smaild.java" version "#VERSION"
            }
        """.trimIndent()

        buildFileContent.replace("#VERSION", getProjectVersion())
        buildFile.writeText(buildFileContent)

        val result = GradleRunner.create()
            .withProjectDir(projectDir.root)
            .withArguments("build")
            .withPluginClasspath()
            .build()

        assertNotNull(result.task(":compileJava"))
        assertNotNull(result.task(":processResources"))
        assertNotNull(result.task(":jar"))
        assertNotNull(result.task(":assemble"))
        assertNotNull(result.task(":compileTestJava"))
        assertNotNull(result.task(":processTestResources"))
        assertNotNull(result.task(":testClasses"))
        assertNotNull(result.task(":test"))

        assertNull(result.task(":javadoc"))
        assertNull(result.task(":javadocJar"))
        assertNull(result.task(":sourcesJar"))

        assertEquals(result.task(":build")?.outcome, SUCCESS)
    }

    @Test
    fun shouldBuildLibrary() {
        val buildFileContent = """
            plugins {
                id "com.smaild.java" version "#VERSION"
            }
        """.trimIndent()

        buildFileContent.replace("#VERSION", getProjectVersion())
        buildFile.writeText(buildFileContent)

        propertiesFile.writeText("""
            java-library = true
        """.trimIndent())

        val result = GradleRunner.create()
            .withProjectDir(projectDir.root)
            .withArguments("build")
            .withPluginClasspath()
            .build()

        assertNotNull(result.task(":compileJava"))
        assertNotNull(result.task(":processResources"))
        assertNotNull(result.task(":jar"))
        assertNotNull(result.task(":javadoc"))
        assertNotNull(result.task(":javadocJar"))
        assertNotNull(result.task(":sourcesJar"))
        assertNotNull(result.task(":assemble"))
        assertNotNull(result.task(":compileTestJava"))
        assertNotNull(result.task(":processTestResources"))
        assertNotNull(result.task(":testClasses"))
        assertNotNull(result.task(":test"))

        assertEquals(result.task(":build")?.outcome, SUCCESS)
    }


    private fun getProjectVersion(): String {
        val properties = Properties()
        val propertiesFile = File("gradle.properties")
        propertiesFile.inputStream().use {
            properties.load(it)
        }

        return properties.getProperty("version")
    }
}
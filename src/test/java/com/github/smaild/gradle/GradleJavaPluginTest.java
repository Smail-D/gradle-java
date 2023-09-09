package com.github.smaild.gradle;

import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.internal.tasks.testing.junitplatform.JUnitPlatformTestFramework;
import org.gradle.internal.impldep.org.apache.commons.io.FileUtils;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GradleJavaPluginTest {

    private Project project;

    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() {
        // Create a test project and apply the plugin
        project = ProjectBuilder.builder().build();
        project.getPlugins().apply("io.github.smaild.gradle-java");
    }

    @Test
    void shouldApplyDefaultPlugins() {
        assertTrue(project.getPluginManager().hasPlugin("java"));
        assertTrue(project.getPluginManager().hasPlugin("jvm-test-suite"));
    }

    @Test
    void shouldReleaseJDK17ByDefault() {
        Assertions.assertEquals(project.getProperties().get("sourceCompatibility"), JavaVersion.VERSION_17);
        Assertions.assertEquals(project.getProperties().get("targetCompatibility"), JavaVersion.VERSION_17);
    }

    @Test
    void shouldUseJunit5() {
        var tests = project.getTasks().withType(org.gradle.api.tasks.testing.Test.class);
        Assertions.assertEquals(tests.getByName("test").getTestFramework().getClass(), JUnitPlatformTestFramework.class);
    }

    @Test
    void shouldRunIntegrationTestInTheCheckTask() throws IOException {
        var buildScript =
                """
                plugins {
                    id "io.github.smaild.gradle-java"
                }
                """;
        File buildSrc = new File(tempDir, "build.gradle");
        FileUtils.writeByteArrayToFile(buildSrc, buildScript.getBytes(StandardCharsets.UTF_8));

        var result = GradleRunner.create()
                .withArguments("check")
                .withProjectDir(tempDir)
                .withPluginClasspath()
                .forwardOutput()
                .build();

        Assertions.assertEquals(12, result.getTasks().size());
        Assertions.assertNotNull(result.task(":compileJava"));
        Assertions.assertNotNull(result.task(":processResources"));
        Assertions.assertNotNull(result.task(":classes"));
        Assertions.assertNotNull(result.task(":compileTestJava"));
        Assertions.assertNotNull(result.task(":processTestResources"));
        Assertions.assertNotNull(result.task(":testClasses"));
        Assertions.assertNotNull(result.task(":test"));
        Assertions.assertNotNull(result.task(":compileIntegrationTestJava"));
        Assertions.assertNotNull(result.task(":processIntegrationTestResources"));
        Assertions.assertNotNull(result.task(":integrationTestClasses"));
        Assertions.assertNotNull(result.task(":integrationTest"));
        Assertions.assertNotNull(result.task(":check"));
    }
}

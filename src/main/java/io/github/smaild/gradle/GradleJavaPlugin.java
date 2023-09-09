package io.github.smaild.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.Dependencies;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.JvmTestSuitePlugin;
import org.gradle.api.plugins.jvm.JvmTestSuite;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.jvm.toolchain.JvmVendorSpec;
import org.gradle.testing.base.TestingExtension;

public class GradleJavaPlugin implements Plugin<Project> {

    private static final int DEFAULT_JAVA_VERSION = 17;

    public void apply(Project project) {

        // Apply and configure Java plugin
        project.getPluginManager().apply(JavaPlugin.class);
        project.getExtensions().configure(JavaPluginExtension.class, java -> {
            // Configure Toolchain
            java.toolchain(toolchain -> {
                toolchain.getVendor().set(JvmVendorSpec.AZUL);
                toolchain.getLanguageVersion().set(JavaLanguageVersion.of(DEFAULT_JAVA_VERSION));
            });
        });

        // Apply Jvm Test suite plugin
        project.getPluginManager().apply(JvmTestSuitePlugin.class);
        project.getExtensions().configure(TestingExtension.class, testing -> {
            var suites = testing.getSuites();
            suites.withType(JvmTestSuite.class).configureEach(JvmTestSuite::useJUnitJupiter);

            // Configure integrationTest suite
            suites.register("integrationTest", JvmTestSuite.class, suite -> {
                suite.dependencies(Dependencies::project);
                suite.getTargets().all(testSuiteTarget -> {
                    var test = project.getTasks().named("test");
                    testSuiteTarget.getTestTask().configure(integrationTest -> integrationTest.shouldRunAfter(test));
                });
            });

            // Add integrationTest task to gradle 'check'
            project.getTasks().named("check")
                    .configure(task -> task.dependsOn(suites.named("integrationTest")));
        });
    }
}

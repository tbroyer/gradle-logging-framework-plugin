package net.ltgt.gradle.logging

import com.google.common.truth.Truth.assertThat
import java.io.File
import org.gradle.testkit.runner.GradleRunner
import org.gradle.util.GradleVersion
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.provider.ArgumentsSources

val testGradleVersion: String = System.getProperty("test.gradle-version", GradleVersion.current().version)

private val constraints =
    """
                (constraints) {
        ${dependencyConstraints.asSequence().map {
        """
                    test("$it")
"""
    }.joinToString(separator = "")}
                }
"""

class LoggingFrameworkPluginFunctionalTest {

    @TempDir lateinit var projectDir: File

    @ParameterizedTest
    @ArgumentsSources(
        ArgumentsSource(ConflictingSlf4jDependencies::class),
        ArgumentsSource(ConflictingLog4jDependencies::class),
        ArgumentsSource(ConflictingJCLDependencies::class)
    )
    fun `base plugin detects conflicts`(dependencies: Set<String>, capability: String) {
        createProject("net.ltgt.logging-framework.conflicts", dependencies)

        // Run the build
        val result = createRunner()
            .buildAndFail()

        // Verify the result
        assertThat(result.output).contains("Cannot select module with conflict on capability '$capability:")
    }

    @ParameterizedTest
    @ArgumentsSources(
        ArgumentsSource(ConflictingFloggerDependencies::class),
        ArgumentsSource(ConflictingJULDependencies::class)
    )
    fun `base plugin does not detect conflicts`(dependencies: Set<String>, capability: String) {
        createProject("net.ltgt.logging-framework.conflicts", dependencies)

        // Run the build
        val result = createRunner()
            .build()

        // Verify the result
        assertThat(result.output).doesNotContain("Cannot select module with conflict on capability '$capability:")
    }

    @ParameterizedTest
    @ArgumentsSources(
        ArgumentsSource(ConflictingFloggerDependencies::class)
    )
    fun `flogger plugin does not detect conflicts`(dependencies: Set<String>, capability: String) {
        createProject("net.ltgt.logging-framework.single-flogger-backend", dependencies)

        // Run the build
        val result = createRunner()
            .buildAndFail()

        // Verify the result
        assertThat(result.output).contains("Cannot select module with conflict on capability '$capability:")
    }

    @ParameterizedTest
    @ArgumentsSources(
        ArgumentsSource(ConflictingJULDependencies::class)
    )
    fun `JUL plugin does not detect conflicts`(dependencies: Set<String>, capability: String) {
        createProject("net.ltgt.logging-framework.single-jul-bridge", dependencies)

        // Run the build
        val result = createRunner()
            .buildAndFail()

        // Verify the result
        assertThat(result.output).contains("Cannot select module with conflict on capability '$capability:")
    }

    private fun createProject(pluginId: String, dependencies: Set<String>) {
        projectDir.resolve("settings.gradle.kts").writeText("")
        projectDir.resolve("build.gradle.kts").writeText(
            """
            plugins {
                id("$pluginId")
            }

            repositories {
                mavenCentral()
            }

            val test by configurations.creating
            dependencies {
                $constraints
                ${dependencies.asSequence().map {
                """
                test("$it")
        """
            }.joinToString(separator = "")}
            }

            tasks {
                register("testDependencies") {
                    doFirst {
                        test.resolve()
                    }
                }
            }
        """
        )
    }

    private fun createRunner() =
        GradleRunner.create()
            .forwardOutput()
            .withGradleVersion(testGradleVersion)
            .withPluginClasspath()
            .withArguments("testDependencies")
            .withProjectDir(projectDir)
}

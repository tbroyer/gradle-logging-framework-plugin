package net.ltgt.gradle.logging

import com.google.common.truth.Truth.assertThat
import java.io.File
import org.gradle.api.GradleException
import org.gradle.api.artifacts.Configuration
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.repositories
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.provider.ArgumentsSources

class LoggingFrameworkPluginTest {

    companion object {
        @JvmStatic
        @TempDir
        lateinit var userHomeDir: File
    }

    @ParameterizedTest
    @ArgumentsSources(
        ArgumentsSource(ConflictingSlf4jDependencies::class),
        ArgumentsSource(ConflictingLog4jDependencies::class),
        ArgumentsSource(ConflictingJCLDependencies::class)
    )
    fun `base plugin detects conflicts`(dependencies: Set<String>, capability: String) {
        val test = createProject("net.ltgt.logging-framework.conflicts", dependencies)

        val exception = assertThrows<GradleException> {
            test.resolve()
        }
        assertThat(exception).hasCauseThat().hasCauseThat().hasMessageThat()
            .contains("Cannot select module with conflict on capability '$capability:")
    }

    @ParameterizedTest
    @ArgumentsSources(
        ArgumentsSource(ConflictingFloggerDependencies::class),
        ArgumentsSource(ConflictingJULDependencies::class)
    )
    fun `base plugin does not detect non-conflicts`(dependencies: Set<String>) {
        val test = createProject("net.ltgt.logging-framework.conflicts", dependencies)

        test.resolve()

        assertThat(test.resolvedDependencies).containsAtLeastElementsIn(dependencies)
    }

    @ParameterizedTest
    @ArgumentsSources(
        ArgumentsSource(ConflictingFloggerDependencies::class)
    )
    fun `flogger plugin does not detect non-conflicts`(dependencies: Set<String>, capability: String) {
        val test = createProject("net.ltgt.logging-framework.single-flogger-backend", dependencies)

        val exception = assertThrows<GradleException> {
            test.resolve()
        }
        assertThat(exception).hasCauseThat().hasCauseThat().hasMessageThat()
            .contains("Cannot select module with conflict on capability '$capability:")
    }

    @ParameterizedTest
    @ArgumentsSources(
        ArgumentsSource(ConflictingJULDependencies::class)
    )
    fun `JUL plugin does not detect non-conflicts`(dependencies: Set<String>, capability: String) {
        val test = createProject("net.ltgt.logging-framework.single-jul-bridge", dependencies)

        val exception = assertThrows<GradleException> {
            test.resolve()
        }
        assertThat(exception).hasCauseThat().hasCauseThat().hasMessageThat()
            .contains("Cannot select module with conflict on capability '$capability:")
    }

    private fun createProject(pluginId: String, dependencies: Set<String>): Configuration {
        val project = ProjectBuilder.builder()
            .withGradleUserHomeDir(userHomeDir)
            .build()
        val test by project.configurations.creating
        with(project) {
            apply(plugin = pluginId)
            repositories {
                mavenCentral()
            }
            dependencies {
                (constraints) {
                    dependencyConstraints.forEach { test(it) }
                }
                dependencies.forEach { test(it) }
            }
        }
        return test
    }

    private val Configuration.resolvedDependencies: Collection<String>
        get() = resolvedConfiguration.resolvedArtifacts.map {
            "${it.moduleVersion.id.group}:${it.moduleVersion.id.name}:${it.moduleVersion.id.version}"
        }
}

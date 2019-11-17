package net.ltgt.gradle.logging

import com.google.common.collect.Sets
import com.google.common.collect.Streams
import java.util.stream.Stream
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.ArgumentsProvider

private fun version(id: Id) = when (id.group) {
    "org.slf4j" -> "1.7.29"
    "ch.qos.logback" -> "1.2.3"
    "org.apache.logging.log4j" -> "2.12.1"
    "com.google.flogger" -> "0.4"
    "log4j" -> "1.2.17"
    "commons-logging" -> "1.2"
    else -> fail("Unknown dependency $id")
}

private fun versionned(id: Id) = "${id.group}:${id.name}:${version(id)}"

val dependencyConstraints = setOf(
    "log4j:log4j:1.2.17" // flogger depends on 1.2.15 which has broken dependencies
)

class ConflictingSlf4jDependencies : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext): Stream<Arguments> = Streams.concat(
        Sets.combinations(slf4jBindings.mapTo(mutableSetOf()) { versionned(it) }, 2).stream()
            .map { arguments(it, SLF4J_BINDING_CAPABILITY.toString()) },
        Stream.of(
            arguments(
                slf4jJulLoop.mapTo(mutableSetOf()) { versionned(it) },
                SLF4J_JUL_LOOP_CAPABILITY.toString()
            ),
            arguments(
                setOf("org.slf4j:slf4j-log4j12:1.7.29", "org.slf4j:log4j-over-slf4j:1.7.29"),
                "log4j:log4j"
            ),
            arguments(
                setOf("org.slf4j:slf4j-jcl:1.7.29", "org.slf4j:jcl-over-slf4j:1.7.29"),
                "commons-logging:commons-logging-api"
            ),
            arguments(
                setOf("org.apache.logging.log4j:log4j-to-slf4j:2.12.1", "org.apache.logging.log4j:log4j-slf4j-impl:2.12.1"),
                LOG4J_IMPLEMENTATION_CAPABILITY.toString()
            ),
            arguments(
                setOf("org.apache.logging.log4j:log4j-to-slf4j:2.12.1", "org.apache.logging.log4j:log4j-slf4j18-impl:2.12.1"),
                LOG4J_IMPLEMENTATION_CAPABILITY.toString()
            )
        )
    )
}

class ConflictingLog4jDependencies : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext): Stream<Arguments> =
        Sets.combinations(log4jImplementations.mapTo(mutableSetOf()) { versionned(it) }, 2).stream()
            .map { arguments(it, LOG4J_IMPLEMENTATION_CAPABILITY.toString()) }
}

class ConflictingFloggerDependencies : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext): Stream<Arguments> =
        // XXX: flogger-log4j2-backend has not yet been released
        Sets.combinations((floggerBackends - Id("com.google.flogger", "flogger-log4j2-backend")).mapTo(mutableSetOf()) { versionned(it) }, 2).stream()
            .map { arguments(it, FLOGGER_BACKEND_CAPABILITY.toString()) }
}

class ConflictingJCLDependencies : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext): Stream<Arguments> =
        Stream.of(
            arguments(
                setOf("commons-logging:commons-logging:1.2", "commons-logging:commons-logging-api:1.1"),
                "commons-logging:commons-logging-api"
            )
        )
}

class ConflictingJULDependencies : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext): Stream<Arguments> =
        Sets.combinations(julBridges.mapTo(mutableSetOf()) { versionned(it) }, 2).stream()
            .map { arguments(it, JUL_BRIDGE_CAPABILITY.toString()) }
}

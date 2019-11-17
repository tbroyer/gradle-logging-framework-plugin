package net.ltgt.gradle.logging

import org.gradle.api.Plugin
import org.gradle.api.Project

open class LoggingFrameworkPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        fun String.providesCapability(capability: Id) =
            withModule(this) { providesCapability(capability) }
        fun Id.providesCapability(capability: Id) = toString().providesCapability(capability)

        slf4jBindings.forEach { it.providesCapability(SLF4J_BINDING_CAPABILITY) }
        slf4jBridges.forEach { k, v -> k.providesCapability(v) }
        slf4jJulLoop.forEach { it.providesCapability(SLF4J_JUL_LOOP_CAPABILITY) }
        log4jImplementations.forEach { it.providesCapability(LOG4J_IMPLEMENTATION_CAPABILITY) }
        log4jBridges.forEach { k, v -> k.providesCapability(v) }

        // Commons Logging (JCL) is special/weird: it's a bridge that provides a few implementations
        // Some versions provide a commons-logging-api artifact that's a subset of commons-logging without the implementations.
        // SLF4J duplicates the API, whereas Log4j 2 plugs as a provider through Service Loader.
        // We handle that by using two capabilities: commons-logging-api and commons-logging.
        "commons-logging:commons-logging".providesCapability(Id("commons-logging", "commons-logging-api"))
    }
}

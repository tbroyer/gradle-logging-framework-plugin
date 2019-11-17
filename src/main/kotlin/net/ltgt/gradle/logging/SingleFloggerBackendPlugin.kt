package net.ltgt.gradle.logging

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

open class SingleFloggerBackendPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        pluginManager.apply(LoggingFrameworkPlugin::class)
        floggerBackends.forEach { withModule(it) { providesCapability(FLOGGER_BACKEND_CAPABILITY) } }
    }
}

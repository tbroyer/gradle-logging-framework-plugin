package net.ltgt.gradle.logging

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

open class SingleJULBridgePlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        pluginManager.apply(LoggingFrameworkPlugin::class)
        julBridges.forEach { withModule(it) { providesCapability(JUL_BRIDGE_CAPABILITY) } }
    }
}

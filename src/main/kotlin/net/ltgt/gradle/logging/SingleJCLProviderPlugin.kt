package net.ltgt.gradle.logging

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

open class SingleJCLProviderPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        pluginManager.apply(LoggingFrameworkPlugin::class)
        jclProviders.forEach { withModule(it) { providesCapability(JCL_PROVIDER_CAPABILITY) } }
    }
}

package net.ltgt.gradle.logging

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.CapabilityResolutionDetails
import org.gradle.api.artifacts.ComponentMetadataDetails
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier

internal fun Project.withModule(id: String, rule: Action<in ComponentMetadataDetails>) =
    dependencies.components.withModule(id, rule)
internal fun Project.withModule(id: Id, rule: Action<in ComponentMetadataDetails>) =
    withModule(id.toString(), rule)

internal fun ComponentMetadataDetails.providesCapability(group: String, name: String, version: String) =
    allVariants { withCapabilities { addCapability(group, name, version) } }
internal fun ComponentMetadataDetails.providesCapability(capability: Id) =
    providesCapability(capability.group, capability.name, id.version)

internal fun Configuration.withCapability(group: String, name: String, rule: Action<in CapabilityResolutionDetails>) =
    resolutionStrategy.capabilitiesResolution.withCapability(group, name, rule)
internal fun Configuration.withCapability(id: Id, rule: Action<in CapabilityResolutionDetails>) =
    withCapability(id.group, id.name, rule)

internal fun CapabilityResolutionDetails.select(group: String, name: String, reason: String? = null) =
    candidates.find { it is ModuleComponentIdentifier && it.group == group && it.module == name }?.let {
        select(it)
        reason?.let { because(it) }
    }
internal fun CapabilityResolutionDetails.select(id: Id, reason: String? = null) =
    select(id.group, id.name, reason)

internal fun List<ComponentIdentifier>.singleOrNull(group: String, name: String) =
    singleOrNull { it is ModuleComponentIdentifier && it.group == group && it.module == name }

package io.github.nickacpt.lightcraft.gradle.providers.minecraft

import io.github.nickacpt.lightcraft.gradle.*
import net.fabricmc.loom.configuration.providers.minecraft.MinecraftVersionMeta
import org.gradle.api.Project
import org.gradle.kotlin.dsl.maven

object MinecraftLibraryProvider {

    fun provideMinecraftLibraries(project: Project) {

        val blacklistedDependencies = listOf("launchwrapper", "asm-all")
        val dependencyVersionOverrides = listOf("2.9.1-nightly-20130708-debug3" to "2.9.1")
        project.repositories.maven(FABRICMC_LIBRARIES_BASE)
        project.repositories.maven(ORIONMAVEN_BASE)
        project.repositories.maven(LIBRARIES_BASE)
        project.repositories.maven(JITPACK_BASE)

        val versionInfo: MinecraftVersionMeta = MinecraftProvider.provideGameVersionMeta(project)

        for (library in versionInfo.libraries ?: emptyList()) {
            if (library.isValidForOS && !library.hasNatives() && library.artifact() != null) {
                if (library.name != null && blacklistedDependencies.none { library.name.contains(it, true) }) {
                    var finalName = library.name
                    dependencyVersionOverrides.forEach {
                        finalName = finalName.replace(it.first, it.second)
                    }

                    if (finalName.contains("lwjgl") && !project.lightCraftExtension.provideOriginalLwjgl) continue

                    project.dependencies.add(
                        MINECRAFT_LIBRARY_CONFIGURATION,
                        finalName
                    )
                }
            }
        }

        // Provide a few updated dependencies
        arrayOf(
            mixinDependency,
            asmDependency,
            asmTreeDependency,
            asmUtilDependency,
            "javax.annotation:javax.annotation-api:1.3.2"
        ).forEach {
            project.dependencies.add(
                UPGRADED_MINECRAFT_LIBRARY_CONFIGURATION,
                it
            )
        }

        // Add OrionCraft's Orion Launcher
        project.dependencies.add(
            ORION_LAUNCHER_CONFIGURATION,
            orionLauncherDependency
        )
    }
}
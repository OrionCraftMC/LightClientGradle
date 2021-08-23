@file:Suppress("LeakingThis")

package io.github.nickacpt.lightcraft.gradle.tasks.impl

import io.github.nickacpt.lightcraft.gradle.*
import io.github.nickacpt.lightcraft.gradle.LightCraftConfigurations.launchWrapperConfiguration
import io.github.nickacpt.lightcraft.gradle.LightCraftConfigurations.minecraftLibraryConfiguration
import io.github.nickacpt.lightcraft.gradle.providers.minecraft.MappedMinecraftProvider
import io.github.nickacpt.lightcraft.gradle.providers.minecraft.MinecraftNativesProvider
import io.github.nickacpt.lightcraft.gradle.utils.getMixinFiles
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.JavaExec
import java.io.File

open class RunClientTask : JavaExec() {

    init {
        group = LIGHTCRAFT_TASK_GROUP

        val extension = project.lightCraftExtension
        setupClasspath()
        setupWorkingDirectory()
        setupMainClassAndArguments(extension)
    }

    private fun setupClasspath() {
        val jarsToDepend = mutableListOf<File>()

        // Depend on the Minecraft jar that is our dependency
        jarsToDepend += MappedMinecraftProvider.provideMappedMinecraftDependency(project)

        // Setup Task to depend on the jar task
        val jarTask = project.tasks.getByName(JavaPlugin.JAR_TASK_NAME)
        dependsOn(jarTask)

        // Depend on the Jars that were outputted by our project
        jarsToDepend += jarTask.outputs.files.files

        // Depend on LaunchWrapper
        jarsToDepend += project.launchWrapperConfiguration.resolve()

        // Depend on the Minecraft libraries
        jarsToDepend += project.minecraftLibraryConfiguration.resolve()

        // Finally, set up our dependency on these files
        classpath(*jarsToDepend.toTypedArray())
    }

    private fun setupWorkingDirectory() {
        workingDir = File(project.projectDir, "run").also { it.mkdirs() }
    }

    private fun setupMainClassAndArguments(extension: LightCraftGradleExtension) {
        // Launch with LaunchWrapper
        mainClass.set("net.minecraft.launchwrapper.Launch")

        setupJvmLaunchArguments(extension)

        setupGameLaunchArguments(extension)
    }

    private fun setupJvmLaunchArguments(extension: LightCraftGradleExtension) {
        val jvmLaunchArguments = mutableListOf<Pair<String, String>>()

        // Tell LaunchWrapper that we are launching Mixins on the client side
        jvmLaunchArguments += LAUNCHWRAPPER_MIXIN_SIDE_PROP to MIXIN_SIDE_CLIENT
        jvmLaunchArguments += LAUNCHWRAPPER_MIXIN_SIDE_PROP to MIXIN_SIDE_CLIENT

        // Provide Minecraft with lwjgl natives
        jvmLaunchArguments += JVM_LIBRARY_PATH_PROP to "\"${MinecraftNativesProvider.provideNativesFolder(project)}\""

        // Tell LaunchWrapper what Minecraft class we are launching
        jvmLaunchArguments += LAUNCHWRAPPER_MAIN_CLASS_PROP to extension.clientVersion.mainClass

        // Finally, set up our task to use these arguments
        jvmArgs = jvmLaunchArguments.map { "-D${it.first}=${it.second}" }
    }

    private fun setupGameLaunchArguments(extension: LightCraftGradleExtension) {
        //TODO: 1.8 arguments

        // Set up game launch arguments
        val launchArguments = mutableListOf<String>()

        // Provide our player's username
        launchArguments += LIGHTCRAFT_LAUNCH_PLAYER_NAME

        // Provide Minecraft session id
        launchArguments += "-"

        // Provide our game directory
        launchArguments += "--gameDir"
        launchArguments += "\"${workingDir.absolutePath}\""

        // Provide Vanilla LaunchWrapper tweaker
        launchArguments += "--tweakClass"
        launchArguments += "net.minecraft.launchwrapper.VanillaTweaker"

        // Provide our custom Mixin LaunchWrapper tweaker
        launchArguments += "--tweakClass"
        launchArguments += "org.spongepowered.asm.launch.LightCraftMixinTweaker"

        // Provide Mixin files to LaunchWrapper
        project.getMixinFiles().forEach { mixinFile ->
            launchArguments += "--mixin"
            launchArguments += mixinFile.name
        }

        // Finally, set up our task to use these arguments
        args = launchArguments
    }

}
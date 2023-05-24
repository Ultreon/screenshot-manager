@file:Suppress("removal")

package com.ultreon.mods.screenshotmanager.forge

import com.ultreon.mods.screenshotmanager.ScreenshotManagerMod
import dev.architectury.platform.forge.EventBuses
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.InterModComms
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent
import thedarkcolour.kotlinforforge.KotlinModLoadingContext
import java.util.stream.Collectors

@Mod(ScreenshotManagerForge.modId)
class ScreenshotManagerForge {
    init {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT) { Runnable {
            val eventBus = KotlinModLoadingContext.get().getKEventBus()
            EventBuses.registerModEventBus(modId, eventBus)

            eventBus.register(::enqueueIMC)
            eventBus.register(::processIMC)

            ScreenshotManagerMod
        } }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun enqueueIMC(event: InterModEnqueueEvent) {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("screenshot-manager", "helloworld") {
            ScreenshotManagerMod.logger.info("Hello world from the MDK")
            return@sendTo "Hello world"
        }
    }

    private fun processIMC(event: InterModProcessEvent) {
        // some example code to receive and process InterModComms from other mods
        ScreenshotManagerMod.logger.info(
            "Got IMC {}",
            event.imcStream.map { m -> m.messageSupplier().get() }.collect(Collectors.toList())
        )
    }

    companion object {
        const val modId = "screenshotmgr"
    }
}
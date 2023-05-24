package com.ultreon.mods.screenshotmanager.fabric

import com.ultreon.mods.screenshotmanager.ScreenshotManagerMod
import net.fabricmc.api.ClientModInitializer

@Suppress("UNUSED")
object ScreenshotManagerFabric : ClientModInitializer {
    override fun onInitializeClient() {
        ScreenshotManagerMod
    }
}
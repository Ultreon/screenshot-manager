package com.ultreon.mods.screenshotmanager

import net.fabricmc.api.ClientModInitializer

@Suppress("UNUSED")
object ScreenshotManagerFabric : ClientModInitializer {
    override fun onInitializeClient() {
        ScreenshotManagerMod
    }
}
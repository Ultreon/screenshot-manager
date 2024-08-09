package com.ultreon.mods.screenshotmanager

import dev.ultreon.quantum.client.ClientModInit

@Suppress("UNUSED")
object ScreenshotManagerFabric : ClientModInit {
    override fun onInitializeClient() {
        ScreenshotManagerMod
    }
}
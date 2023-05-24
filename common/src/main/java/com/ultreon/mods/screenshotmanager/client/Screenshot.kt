package com.ultreon.mods.screenshotmanager.client

import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.resources.ResourceLocation
import java.io.File

@JvmRecord
data class Screenshot(
    val file: File,
    val texture: AbstractTexture?,
    val resourceLocation: ResourceLocation?,
    val data: ScreenshotData
) {
    fun dispose() {
        texture?.releaseId()
    }
}
package com.ultreon.mods.screenshotmanager.client

import com.badlogic.gdx.graphics.Texture
import dev.ultreon.quantum.util.NamespaceID
import java.io.File

@JvmRecord
data class Screenshot(
    val file: File,
    val texture: Texture?,
    val id: NamespaceID?,
    val data: ScreenshotData
) {
    fun dispose() {
        texture?.dispose()
    }
}
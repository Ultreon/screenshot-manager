package com.ultreon.mods.screenshotmanager.client

import com.badlogic.gdx.graphics.Texture
import dev.ultreon.quantum.util.Identifier
import java.io.File

@JvmRecord
data class Screenshot(
    val file: File,
    val texture: Texture?,
    val id: Identifier?,
    val data: ScreenshotData
) {
    fun dispose() {
        texture?.dispose()
    }
}
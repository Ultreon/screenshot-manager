package com.ultreon.mods.screenshotmanager.ext

import com.ultreon.mods.screenshotmanager.ScreenshotManagerMod.id
import dev.ultreon.quantum.client.gui.Renderer

fun Renderer.drawFrame(x: Int, y: Int, w: Int = 10, h: Int = 10) {
    this.draw9Slice(id("textures/gui/frame.png"), x, y, w, h, 0, 0, 21, 21, 7, 21, 21)
}

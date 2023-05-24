package com.ultreon.mods.screenshotmanager.client

import net.minecraft.client.renderer.texture.DynamicTexture
import java.io.File

object ScreenshotCache {
    private val cache: MutableMap<File, ScreenshotData> = HashMap()
    fun cache(name: File, texture: DynamicTexture): ScreenshotData {
        val pixels = texture.pixels
        val data: ScreenshotData = if (pixels == null) {
            ScreenshotData(0, 0)
        } else {
            ScreenshotData(pixels.width, pixels.height)
        }
        cache[name] = data
        return data
    }

    operator fun get(file: File): ScreenshotData? {
        return cache[file]
    }

    @Deprecated("Replaced with getOrEmpty.", ReplaceWith(
        "getOrEmpty(file)",
        "com.ultreon.mods.screenshotmanager.client.ScreenshotCache.getOrEmpty"
    ))
    fun getOrDefault(file: File): ScreenshotData {
        return getOrEmpty(file)
    }

    fun getOrEmpty(file: File): ScreenshotData {
        return cache.getOrDefault(file, ScreenshotData(16, 16))
    }

    fun isCached(file: File): Boolean {
        return cache.containsKey(file)
    }

    @Deprecated("Object class now.", ReplaceWith("ScreenshotCache"))
    fun get(): ScreenshotCache {
        return ScreenshotCache
    }
}

package com.ultreon.mods.screenshotmanager.text

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

object CommonTexts {
    val loading: MutableComponent = Component.translatable("screen.screenshotmgr.loading")
    val prev: MutableComponent = Component.translatable("misc.screenshotmgr.prev")
    val next: MutableComponent = Component.translatable("misc.screenshotmgr.next")
    val noScreenshots: MutableComponent = Component.translatable("screen.screenshotmgr.no_screenshots")
    val errorOccurred: MutableComponent = Component.translatable("screen.screenshotmgr.error_occurred")
    val invalidScreenshot: MutableComponent = Component.translatable("screen.screenshotmgr.invalid_screenshot")
}
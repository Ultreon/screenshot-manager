package com.ultreon.mods.screenshotmanager.text

import dev.ultreon.quantum.text.MutableText
import dev.ultreon.quantum.text.TextObject

object CommonTexts {
    val loading: MutableText = TextObject.translation("screen.screenshotmgr.loading")
    val prev: MutableText = TextObject.translation("misc.screenshotmgr.prev")
    val next: MutableText = TextObject.translation("misc.screenshotmgr.next")
    val noScreenshots: MutableText = TextObject.translation("screen.screenshotmgr.no_screenshots")
    val errorOccurred: MutableText = TextObject.translation("screen.screenshotmgr.error_occurred")
    val invalidScreenshot: MutableText = TextObject.translation("screen.screenshotmgr.invalid_screenshot")
}
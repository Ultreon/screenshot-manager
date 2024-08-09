package com.ultreon.mods.screenshotmanager

import com.ultreon.mods.screenshotmanager.client.gui.screens.ScreenshotsScreen
import com.ultreon.mods.screenshotmanager.client.gui.widgets.ImageButton
import dev.ultreon.quantum.client.QuantumClient
import dev.ultreon.quantum.client.api.events.gui.ScreenEvents
import dev.ultreon.quantum.client.api.events.gui.WidgetEvents
import dev.ultreon.quantum.client.gui.Position
import dev.ultreon.quantum.client.gui.Screen
import dev.ultreon.quantum.client.gui.screens.PauseScreen
import dev.ultreon.quantum.client.gui.screens.TitleScreen
import dev.ultreon.quantum.events.api.ValueEventResult
import dev.ultreon.quantum.text.TextObject
import dev.ultreon.quantum.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// The value here should match an entry in the META-INF/mods.toml file
object ScreenshotManagerMod {
    const val MOD_ID = "screenshotmgr"
    val logger: Logger = LoggerFactory.getLogger("ScreenshotManager")

    fun addButton(screen: Screen) {
        screen.add(ImageButton(screen.width - 40,  screen.height - 40, 15, 15, 0, 0, id("textures/gui/widgets.png")) {
            ScreenshotsScreen(TextObject.translation("screen.screenshotmgr.menu")).open()
        }).onRevalidate {
            it.setPos(screen.width - 20, 5)
            it.setSize(15, 15)
        }
    }

    @JvmStatic
    fun id(path: String): Identifier {
        return Identifier(MOD_ID, path)
    }
}
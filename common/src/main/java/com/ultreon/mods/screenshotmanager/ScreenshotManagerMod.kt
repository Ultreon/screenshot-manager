package com.ultreon.mods.screenshotmanager

import com.ultreon.mods.screenshotmanager.client.gui.screens.ScreenshotsScreen
import dev.architectury.event.events.client.ClientGuiEvent
import net.minecraft.client.gui.components.ImageButton
import net.minecraft.client.gui.screens.PauseScreen
import net.minecraft.client.gui.screens.TitleScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// The value here should match an entry in the META-INF/mods.toml file
object ScreenshotManagerMod {
    const val modId = "screenshotmgr"
    val logger: Logger = LoggerFactory.getLogger("ScreenshotManager")

    init {
        ClientGuiEvent.INIT_POST.register { screen, access ->
            if (screen is TitleScreen || screen is PauseScreen) {
                access.addRenderableWidget(ImageButton(screen.width - 20, 5, 15, 15, 0, 0, res("textures/gui/widgets.png")) {
                    ScreenshotsScreen(Component.translatable("screen.screenshotmgr.menu")).open()
                })
            }
        }
    }

    @JvmStatic
    fun res(path: String): ResourceLocation {
        return ResourceLocation(modId, path)
    }
}
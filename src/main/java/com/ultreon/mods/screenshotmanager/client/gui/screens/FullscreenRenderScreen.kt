@file:Suppress("UnstableApiUsage")

package com.ultreon.mods.screenshotmanager.client.gui.screens

import com.ultreon.mods.screenshotmanager.client.gui.widgets.ToolBarItem
import com.ultreon.mods.screenshotmanager.client.gui.widgets.Toolbar
import dev.ultreon.quantum.client.gui.GuiBuilder
import dev.ultreon.quantum.client.gui.Renderer
import dev.ultreon.quantum.client.gui.Screen
import dev.ultreon.quantum.text.TextObject
import dev.ultreon.quantum.util.RgbColor

abstract class FullscreenRenderScreen(title: TextObject) : Screen(title) {
    private val toolbar = Toolbar(0)

    fun addToolbarItem(item: ToolBarItem) {
        toolbar.add(item)
    }

    override fun renderBackground(renderer: Renderer) {
        super.renderBackground(renderer)
    }

    override fun renderSolidBackground(renderer: Renderer) {
        super.renderSolidBackground(renderer)

        renderer.fill(0, 0, size.width, size.height, RgbColor.BLACK.withAlpha(0x40))
    }

    override fun build(builder: GuiBuilder) {
        for (item in toolbar.children()) {
            builder.add(item)
        }

        builder.add(toolbar)
    }
}

class FullscreenGuiBuilder(val builder: GuiBuilder) : GuiBuilder(builder.screen()) {
    internal val toolbarItems = mutableListOf<ToolBarItem>()

    fun <T : ToolBarItem> add(item: T): T {
        toolbarItems.add(item)
        return item
    }
}

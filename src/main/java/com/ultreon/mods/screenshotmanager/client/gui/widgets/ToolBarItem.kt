package com.ultreon.mods.screenshotmanager.client.gui.widgets

import dev.ultreon.quantum.client.gui.Bounds
import dev.ultreon.quantum.client.gui.Position
import dev.ultreon.quantum.client.gui.Renderer
import dev.ultreon.quantum.client.gui.widget.Widget
import java.util.function.Supplier

abstract class ToolBarItem(x: Int, y: Int, width: Int, height: Int) : Widget(80, 21) {
    lateinit var toolbar: Toolbar

    abstract override fun renderWidget(renderer: Renderer, mouseX: Int, mouseY: Int, deltaTime: Float)

    override fun position(position: Supplier<Position>): Widget {
        onRevalidate {
            this.setPos(position.get())
        }

        return this
    }

    override fun bounds(position: Supplier<Bounds>): Widget {
        onRevalidate {
            this.setBounds(position.get())
        }

        return this
    }
}
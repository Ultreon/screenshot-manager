package com.ultreon.mods.screenshotmanager.client.gui.widgets

import com.ultreon.mods.screenshotmanager.ext.drawFrame
import dev.ultreon.quantum.client.gui.Bounds
import dev.ultreon.quantum.client.gui.Position
import dev.ultreon.quantum.client.gui.Renderer
import dev.ultreon.quantum.client.gui.widget.UIContainer
import java.util.function.Supplier

class Toolbar(width: Int, height: Int = 30) : UIContainer<Toolbar>(width, height) {
    private val toolbarItems = mutableListOf<ToolBarItem>()

    fun add(item: ToolBarItem) {
        toolbarItems.add(item)

        super.add(item)

        item.toolbar = this
    }

    fun remove(item: ToolBarItem) {
        toolbarItems.remove(item)

        super.remove(item)
    }

    override fun position(position: Supplier<Position>): Toolbar {
        this.onRevalidate {
            this.setPos(position.get())
        }

        return this
    }

    override fun bounds(bounds: Supplier<Bounds>): Toolbar {
        this.onRevalidate {
            this.setBounds(bounds.get())
        }

        return this
    }

    override fun render(renderer: Renderer, mouseX: Int, mouseY: Int, deltaTime: Float) {
        var width = 0
        var height = 0
        var isFirst = true
        for (item in children()) {
            if (item is ToolBarItem) {
                if (!isFirst) {
                    width += 2
                }
                isFirst = false
                width += item.width
                height = maxOf(height, item.height)
            }
        }

        height -= 1

        var x = root.width / 2 - width / 2
        this.y(root.height - height - 21)
        this.x(x)
        isFirst = true
        for (item in children()) {
            if (item is ToolBarItem) {
                if (!isFirst) {
                    x += 2
                }
                isFirst = false
                item.x(x)
                item.y(y - 1)
                x += item.width
            }
        }

        this.width(width)
        this.height(height)

        renderer.drawFrame(this.x - 7, this.y - 7, width + 14, height + 14)

        toolbarItems.forEach { it.render(renderer, mouseX, mouseY, deltaTime) }
    }
}

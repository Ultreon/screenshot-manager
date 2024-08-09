package com.ultreon.mods.screenshotmanager.client.gui.widgets

import dev.ultreon.quantum.client.gui.Bounds
import dev.ultreon.quantum.client.gui.Position
import dev.ultreon.quantum.client.gui.Renderer
import dev.ultreon.quantum.client.gui.widget.Button
import dev.ultreon.quantum.util.Identifier
import java.util.function.Supplier

class ImageButton(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    private val u: Int,
    private val v: Int,
    private val tex: Identifier,
    private val function: () -> Unit
) : Button<ImageButton>(width, height) {
    override fun position(position: Supplier<Position>): Button<ImageButton> {
        onRevalidate {
            this.setPos(position.get())
        }

        return this
    }

    override fun bounds(position: Supplier<Bounds>): Button<ImageButton> {
        onRevalidate {
            this.setBounds(position.get())
        }

        return this
    }

    override fun mouseMove(mouseX: Int, mouseY: Int) {
        super.mouseMove(mouseX, mouseY)
    }

    override fun renderWidget(renderer: Renderer, mouseX: Int, mouseY: Int, deltaTime: Float) {
        super.renderWidget(renderer, mouseX, mouseY, deltaTime)

        if (this.isWithinBounds(mouseX, mouseY)) {
            renderer.blit(
                tex,
                x.toFloat(),
                y.toFloat(),
                width.toFloat(),
                height.toFloat(),
                u.toFloat(),
                v.toFloat() + height.toFloat()
            )
        } else {
            renderer.blit(tex, x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), u.toFloat(), v.toFloat())
        }
    }

    override fun click(): Boolean {
        function()
        return true
    }


}

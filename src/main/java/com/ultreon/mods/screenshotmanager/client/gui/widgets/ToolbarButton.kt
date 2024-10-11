package com.ultreon.mods.screenshotmanager.client.gui.widgets

import com.badlogic.gdx.Input
import dev.ultreon.quantum.client.gui.Renderer
import dev.ultreon.quantum.sound.event.SoundEvents
import dev.ultreon.quantum.text.TextObject
import dev.ultreon.quantum.util.NamespaceID

open class ToolbarButton(
    x: Int,
    y: Int,
    width: Int,
    height: Int = 21,
    private val message: TextObject = TextObject.literal("..."),
    private val onClick: () -> Unit = {}
) : ToolBarItem(x, y, width, height) {
    private var wasPressed: Boolean = false

    var pressed: Boolean = false
        protected set

    fun click(): Boolean {
        if (!this.isEnabled) return false
        this.onClick()
        return true
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, button: Int, clicks: Int): Boolean {
        if (!this.isWithinBounds(mouseX, mouseY)) return false
        if (button == Input.Buttons.LEFT) return this.click()

        return super.mouseClick(mouseX, mouseY, button, clicks)
    }

    override fun renderWidget(renderer: Renderer, mouseX: Int, mouseY: Int, deltaTime: Float) {
        if (!isHovered && pressed) {
            this.pressed = false
        }
        var u = if (this.isEnabled) if (this.isWithinBounds(mouseX, mouseY)) 21 else 0
        else 42
        var v = if (this.pressed) 21 else 0

        u += 63 * 2
        v += 42 * 0

        renderer.draw9Slice(
            NamespaceID("textures/gui/widgets.png"), x, y,
            size.width, size.height, u, v, 21, 21, 5, 256, 256
        )

        if (renderer.pushScissors(this.x, this.y, this.width, this.height)) {
            renderer.textCenter(
                message,
                this.x + this.width / 2,
                this.y + this.height / 2 - 4 + (if (this.pressed) 3 else 0)
            )
            renderer.popScissors()
        }

        if (!pressed && wasPressed) {
            this.wasPressed = false
            client.playSound(SoundEvents.BUTTON_RELEASE, 1.0f)
        }
    }

    override fun mouseRelease(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (button == Input.Buttons.LEFT) {
            this.pressed = false
        }
        return super.mouseRelease(mouseX, mouseY, button)
    }

    override fun mousePress(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (button == Input.Buttons.LEFT) {
            this.pressed = true
            this.wasPressed = true

            client.playSound(SoundEvents.BUTTON_PRESS, 1.0f)
        }

        return super.mousePress(mouseX, mouseY, button)
    }
}
package com.ultreon.mods.screenshotmanager.util

import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.Minecraft
import org.lwjgl.glfw.GLFW

/**
 * Keyboard helper.
 * Check for holding shift, ctrl or alt.
 *
 * @author Qboi123
 */
@Suppress("unused")
object KeyboardHelper {
    private val WINDOW = Minecraft.getInstance().window.window

    @get:Environment(EnvType.CLIENT)
    val isHoldingShift: Boolean
        get() = InputConstants.isKeyDown(WINDOW, GLFW.GLFW_KEY_LEFT_SHIFT) || InputConstants.isKeyDown(
            WINDOW,
            GLFW.GLFW_KEY_RIGHT_SHIFT
        )

    @get:Environment(EnvType.CLIENT)
    val isHoldingCtrl: Boolean
        get() = InputConstants.isKeyDown(WINDOW, GLFW.GLFW_KEY_LEFT_CONTROL) || InputConstants.isKeyDown(
            WINDOW,
            GLFW.GLFW_KEY_RIGHT_CONTROL
        )

    @get:Environment(EnvType.CLIENT)
    val isHoldingAlt: Boolean
        get() = InputConstants.isKeyDown(WINDOW, GLFW.GLFW_KEY_LEFT_ALT) || InputConstants.isKeyDown(
            WINDOW,
            GLFW.GLFW_KEY_RIGHT_ALT
        )
}
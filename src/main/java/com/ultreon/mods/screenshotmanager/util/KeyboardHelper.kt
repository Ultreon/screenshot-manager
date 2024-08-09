package com.ultreon.mods.screenshotmanager.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import dev.ultreon.quantum.client.QuantumClient
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

/**
 * Keyboard helper.
 * Check for holding shift, ctrl or alt.
 *
 * @author XyperCode
 */
@Suppress("unused")
object KeyboardHelper {
    private val WINDOW = QuantumClient.get().window

    @get:Environment(EnvType.CLIENT)
    val isHoldingShift: Boolean
        get() = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)

    @get:Environment(EnvType.CLIENT)
    val isHoldingCtrl: Boolean
        get() = Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT)

    @get:Environment(EnvType.CLIENT)
    val isHoldingAlt: Boolean
        get() = Gdx.input.isKeyPressed(Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Keys.ALT_RIGHT)
}
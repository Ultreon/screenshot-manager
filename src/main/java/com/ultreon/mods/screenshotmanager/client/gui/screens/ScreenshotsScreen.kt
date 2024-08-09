@file:Suppress("UnstableApiUsage")

package com.ultreon.mods.screenshotmanager.client.gui.screens

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.ultreon.mods.screenshotmanager.ScreenshotManagerMod
import com.ultreon.mods.screenshotmanager.client.Screenshot
import com.ultreon.mods.screenshotmanager.client.ScreenshotCache
import com.ultreon.mods.screenshotmanager.client.gui.widgets.ToolbarButton
import com.ultreon.mods.screenshotmanager.text.CommonTexts
import com.ultreon.mods.screenshotmanager.util.KeyboardHelper
import com.ultreon.mods.screenshotmanager.util.Resizer
import dev.ultreon.quantum.client.QuantumClient
import dev.ultreon.quantum.client.gui.Renderer
import dev.ultreon.quantum.text.TextObject
import dev.ultreon.quantum.util.Identifier
import dev.ultreon.quantum.util.RgbColor
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

private val emptyId = Identifier("")

class ScreenshotsScreen(title: TextObject) : FullscreenRenderScreen(title) {
    // No getter / setter.
    private val files0: MutableList<File> = ArrayList()
    var screenshot: Screenshot? = null
        private set

    // Getter & setter.
    var index = 0
        private set
    var total = 0
        private set
    var isLoading = false
        private set

    init {
        reload()

        addToolbarItem(ToolbarButton(0, 0, 50, message = CommonTexts.prev) { prevShot() })
        addToolbarItem(ToolbarButton(0, 0, 50, message = CommonTexts.next) { nextShot() })
    }

    /**
     * Reload the list of screenshots from the "screenshots" directory in the game folder.
     *
     * This function is called internally when the class is initialized.
     * This function does not need to be called manually.
     *
     * @return [Unit].
     */
    private fun reload() {
        val dir = File(QuantumClient.getGameDir().toFile(), "screenshots")

        if (dir.exists()) files0 += dir.listFiles()!!

        this.total = this.files0.size
        this.index = 0
        this.refresh()
    }

    /**
     * Load the screenshot at the current index.
     */
    private fun loadScreenshot() {
        val active = AtomicBoolean(true)
        if (this.files0.size == 0) return
        this.files0[this.index].let { file ->
            active.set(true)
            QuantumClient.invoke {
                // Extract the filename, format it and create the identifier
                val fixedFilename = file.name.lowercase().replace("[^a-z0-9/._]".toRegex(), "_")
                val id = "screenshot/$fixedFilename"
                val location = ScreenshotManagerMod.id(id)

                // Check if the texture is already loaded
                val texture = if (QuantumClient.get().textureManager.isTextureLoaded(location)) {
                    QuantumClient.get().textureManager.getTexture(location)
                } else {
                    // If not, load the texture and cache it
                    val dynamicTexture = loadTexture(location, file)
                    ScreenshotCache.cache(file, dynamicTexture)
                    dynamicTexture
                }

                // Retrieve the cached data
                val data = requireNotNull(ScreenshotCache[file]) { "Screenshot ${file.path} wasn't cached." }

                // Create the screenshot object and update the field
                val screenshot = Screenshot(file, texture, location, data)
                this.screenshot = screenshot
            }
        }
    }

    /**
     * Refresh the screenshot cache.
     */
    fun refresh() {
        thread {
            loadScreenshot()
        }
    }

    /**
     * Render the background of the screen.
     */
    override fun renderBackground(renderer: Renderer) {
        if (client!!.world != null) {
            renderTransparentBackground(renderer)
        } else {
            renderSolidBackground(renderer)
        }


        renderer.pushMatrix()
        renderer.translate(0f, 0f, 100f)
        when {
            this.screenshot != null -> renderScreenshot(renderer)
            this.files0.isNotEmpty() && this.isLoading -> {
                renderer.textCenter(CommonTexts.loading, 2f, this.width / 2, this.height / 2 - 14)
            }

            this.files0.isEmpty() -> {
                renderer.textCenter(
                    CommonTexts.noScreenshots,
                    2f,
                    this.width / 2,
                    this.height / 2
                )
            }

            else -> {
                renderer.textCenter(
                    CommonTexts.errorOccurred,
                    2f,
                    this.width / 2,
                    this.height / 2 - 14
                )
                renderer.textCenter(
                    CommonTexts.invalidScreenshot,
                    this.width / 2,
                    this.height / 2
                )
            }
        }
        renderer.popMatrix()
    }

    private fun renderScreenshot(renderer: Renderer) {
        val texture = this.screenshot!!.texture
        val data = this.screenshot!!.data
        var location = this.screenshot!!.id
        if (location == null) {
            location = emptyId
        }

        if (texture != null) {
            // Calculate the size of the thumbnail
            val imgWidth = data.width
            val imgHeight = data.height
            val resizer = Resizer(imgWidth.toFloat(), imgHeight.toFloat())
            val size = resizer.thumbnail(this.width.toFloat() - 26f, this.height.toFloat() - 82f)
            val centerX = this.width / 2
            val centerY = (this.height - 69) / 2 + 48 / 2
            val width = size.width.toInt()
            val height = size.height.toInt()

            // Draw a black outline around the thumbnail
            // (this is a little easier to see than the slight border around the image)
            renderer.fill(
                centerX - width / 2 - 1,
                centerY - height / 2 - 1,
                width + 2,
                height + 2,
                RgbColor.BLACK
            )

            // Draw the thumbnail
            renderer.blit(
                location,
                centerX - width / 2f, // x position in the top left corner of the thumbnail
                centerY - height / 2f, // y position in the top left corner of the thumbnail
                width.toFloat(), // width of thumbnail
                height.toFloat(), // height of thumbnail
                0f, // u position in the top left corner of the image
                0f, // v position in the top left corner of the image
                imgWidth.toFloat(), // width of image
                imgHeight.toFloat(), // height of image
                imgWidth, // width of image texture
                imgHeight // height of image texture
            )
        } else {
            renderer.blit(
                location,
                0f,
                0f,
                this.width.toFloat(),
                this.height.toFloat(),
                0f,
                0f,
                16f,
                16f,
                16,
                16
            )
        }
    }

    /**
     * Load a texture file into a resource location.
     *
     * @param location the resource location to read the texture into.
     * @param file     the file to read.
     * @return an instance of [Texture] containing data of the given file.
     */
    fun loadTexture(location: Identifier, file: File): Texture {
        try {
            FileInputStream(file).use { input ->
                val readAllBytes = input.readAllBytes()
                val pixmap = Pixmap(readAllBytes, 0, readAllBytes.size)
                val texture = Texture(pixmap)
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
                val mc = QuantumClient.get()
                mc.textureManager.registerTexture(location, texture)
                return texture
            }
        } catch (exception: IOException) {
            ScreenshotManagerMod.logger.error("Couldn't read image: {}", file.absolutePath)
            throw exception
        }
    }


    /**
     * Handles key press events.
     *
     * This implementation handles the left and right arrow keys.
     *
     * @param keyCode the key code of the key that was pressed.
     * @return true if the event was handled, false otherwise.
     */
    override fun keyRelease(keyCode: Int): Boolean {
        if (keyCode == Keys.LEFT) {
            this.prevShot()
            return true
        }
        if (keyCode == Keys.RIGHT) {
            this.nextShot()
            return true
        }

        return super.keyRelease(keyCode)
    }


    /**
     * Handles key press events.
     *
     * This implementation handles the left and right arrow keys.
     *
     * @param keyCode the key code of the key that was pressed.
     * @return true if the event was handled, false otherwise.
     */
    override fun keyPress(keyCode: Int): Boolean {
        if (KeyboardHelper.isHoldingCtrl) {
            if (keyCode == 61 || keyCode == 334) {
                println("Zooming in...")
                return true
            }
            if (keyCode == 45 || keyCode == 333) {
                println("Zooming out...")
                return true
            }
        }

        return super.keyPress(keyCode)
    }

    /**
     * Go to the previous screenshot.
     */
    fun prevShot() {
        if (this.index > 0) {
            this.index--
            this.refresh()
        }
    }

    /**
     * Go to the next screenshot.
     */
    fun nextShot() {
        if (this.index < this.files0.size - 1) {
            this.index++
            this.refresh()
        }
    }

    fun open() {
        this.client.showScreen(this)
    }
}
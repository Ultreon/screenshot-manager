package com.ultreon.mods.screenshotmanager.client.gui.screens

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.ultreon.mods.screenshotmanager.ScreenshotManagerMod
import com.ultreon.mods.screenshotmanager.client.Screenshot
import com.ultreon.mods.screenshotmanager.client.ScreenshotCache
import com.ultreon.mods.screenshotmanager.client.ScreenshotData
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

    val files: List<File> get() = files0.toList()

    // Getter & setter.
    var index = 0
        private set
    var total = 0
        private set
    var loaded = false
        private set
    var isLoading = false
        private set

    init {
        reload()

        addToolbarItem(ToolbarButton(0, 0, 50, message = CommonTexts.prev) { prevShot() })
        addToolbarItem(ToolbarButton(0, 0, 50, message = CommonTexts.next) { nextShot() })
    }

    private fun reload() {
        val dir = File(QuantumClient.getGameDir().toFile(), "screenshots")

        if (dir.exists()) files0 += dir.listFiles()!!

        this.total = this.files0.size
        this.index = 0
        this.refresh()
    }

    private fun loadScreenshot() {
        val active = AtomicBoolean(true)
        if (this.files0.size == 0) return
        this.files0[this.index].let { file ->
            active.set(true)
            QuantumClient.invoke {
                val texture: Texture?
                val data: ScreenshotData
                val location: Identifier?
                val fixedFilename = file.name.lowercase().replace("[^a-z0-9/._]".toRegex(), "_")
                val id = "screenshot/$fixedFilename"
                location = ScreenshotManagerMod.id(id)
                if (!QuantumClient.get().textureManager.isTextureLoaded(location)) {
                    val dynamicTexture = loadTexture(location, file)
                    texture = dynamicTexture
                    data = ScreenshotCache.cache(file, dynamicTexture)
                } else {
                    texture = QuantumClient.get().textureManager.getTexture(location)
                    data = requireNotNull(ScreenshotCache[file]) { "Screenshot ${file.path} wasn't cached." }
                }
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

    override fun renderBackground(graphics: Renderer) {
        if (client!!.world != null) {
            renderTransparentBackground(graphics)
        } else {
            renderSolidBackground(graphics)
        }


        graphics.pushMatrix()
        run {
            graphics.translate(0f, 0f, 100f)
            if (this.screenshot != null) {
                val texture = this.screenshot!!.texture
                val data = this.screenshot!!.data
                var location = this.screenshot!!.id
                if (location == null) {
                    location = emptyId
                }

                if (texture != null) {
                    val imgWidth = data.width
                    val imgHeight = data.height
                    val resizer = Resizer(imgWidth.toFloat(), imgHeight.toFloat())
                    val size = resizer.thumbnail(this.width.toFloat() - 26, this.height.toFloat() - 82)
                    val centerX = this.width / 2
                    val centerY = (this.height - 69) / 2 + 48 / 2
                    val width = size.width.toInt()
                    val height = size.height.toInt()

                    graphics.fill(centerX - width / 2 - 1, centerY - height / 2 - 1, width + 2, height + 2, RgbColor.BLACK)

                    graphics.blit(
                        location,
                        centerX - width / 2f,
                        centerY - height / 2f,
                        width.toFloat(),
                        height.toFloat(),
                        0f,
                        0f,
                        imgWidth.toFloat(),
                        imgHeight.toFloat(),
                        imgWidth,
                        imgHeight
                    )
                } else {
                    graphics.blit(location, 0f, 0f, this.width.toFloat(), this.height.toFloat(), 0f, 0f, 16f, 16f, 16, 16)
                }
            } else if (this.files0.isNotEmpty() && this.isLoading) {
                graphics.textCenter(CommonTexts.loading, 2f, this.width / 2, this.height / 2 - 14)
            } else if (this.files0.isEmpty()) {
                graphics.textCenter(
                    CommonTexts.noScreenshots,
                    2f,
                    this.width / 2,
                    this.height / 2
                )
            } else {
                graphics.textCenter(
                    CommonTexts.errorOccurred,
                    2f,
                    this.width / 2,
                    this.height / 2 - 14
                )
                graphics.textCenter(
                    CommonTexts.invalidScreenshot,
                    this.width / 2,
                    this.height / 2
                )
            }
        }
        graphics.popMatrix()
    }

    /**
     * Load texture file into a resource location.
     *
     * @param location the resource location to read the texture into.
     * @param file     the file to read.
     * @return an instance of [DynamicTexture] containing data of the given file.
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
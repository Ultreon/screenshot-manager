package com.ultreon.mods.screenshotmanager.client.gui.screens

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.ultreon.mods.lib.client.gui.screen.FullscreenRenderScreen
import com.ultreon.mods.lib.client.gui.widget.toolbar.ToolbarButton
import com.ultreon.mods.screenshotmanager.ScreenshotManagerMod
import com.ultreon.mods.screenshotmanager.client.Screenshot
import com.ultreon.mods.screenshotmanager.client.ScreenshotCache
import com.ultreon.mods.screenshotmanager.client.ScreenshotData
import com.ultreon.mods.screenshotmanager.text.CommonTexts
import com.ultreon.mods.screenshotmanager.util.KeyboardHelper
import com.ultreon.mods.screenshotmanager.util.Resizer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

class ScreenshotsScreen(title: Component) : FullscreenRenderScreen(title) {
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
    private var loadThread: Thread? = null

    init {
        reload()

        addToolbarItem(ToolbarButton(0, 0, 50, CommonTexts.prev) { prevShot() })
        addToolbarItem(ToolbarButton(0, 0, 50, CommonTexts.next) { nextShot() })
    }

    private fun reload() {
        val dir = File(Minecraft.getInstance().gameDirectory, "screenshots")

        if (dir.exists()) files0 += dir.listFiles()!!

        this.total = this.files0.size
        this.index = 0
        this.refresh()
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun loadScreenshot() {
        val active = AtomicBoolean(true)
        this.files0[this.index].let { file ->
            active.set(true)
            RenderSystem.recordRenderCall {
                val texture: AbstractTexture?
                val data: ScreenshotData
                val location: ResourceLocation?
                val fixedFilename = file.name.lowercase().replace("[^a-z0-9/._-]".toRegex(), "_")
                val id = "screenshot/$fixedFilename"
                location = ScreenshotManagerMod.res(id)
                val tex: AbstractTexture? = Minecraft.getInstance().textureManager.getTexture(location, null)
                if (tex == null) {
                    val dynamicTexture = loadTexture(location, file)
                    texture = dynamicTexture
                    data = ScreenshotCache.cache(file, dynamicTexture)
                } else {
                    texture = tex
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

    override fun renderBackground(poseStack: PoseStack) {
        if (minecraft!!.level != null) {
            fillGradient(poseStack, 0, 0, width, height, -1072689136, -804253680)
        } else {
            renderDirtBackground(poseStack)
        }


        poseStack.pushPose()
        run {
            poseStack.translate(0.0, 0.0, 100.0)
            if (this.screenshot != null) {
                val texture = this.screenshot!!.texture
                val data = this.screenshot!!.data
                var location = this.screenshot!!.resourceLocation
                if (location == null) {
                    location = ResourceLocation("")
                }
                RenderSystem.setShaderTexture(0, location)
                if (texture != null) {
                    val imgWidth = data.width
                    val imgHeight = data.height
                    val resizer = Resizer(imgWidth.toFloat(), imgHeight.toFloat())
                    val size = resizer.thumbnail(this.width.toFloat() - 26, this.height.toFloat() - 82)
                    val centerX = this.width / 2
                    val centerY = (this.height - 69) / 2 + 48 / 2
                    val width = size.width.toInt()
                    val height = size.height.toInt()

                    fill(poseStack, centerX - width / 2 - 1, centerY - height / 2 - 1, width + centerX - width / 2 + 1, height + centerY - height / 2 + 1, 0xff000000u.toInt())

                    blit(
                        poseStack,
                        centerX - width / 2,
                        centerY - height / 2,
                        width,
                        height,
                        0f,
                        0f,
                        imgWidth,
                        imgHeight,
                        imgWidth,
                        imgHeight
                    )
                } else {
                    blit(poseStack, 0, 0, this.width, this.height, 0f, 0f, 16, 16, 16, 16)
                }
            } else if (this.files0.isNotEmpty() && this.isLoading) {
                poseStack.pushPose()
                run {
                    poseStack.scale(2f, 2f, 1f)
                    drawCenteredString(poseStack, this.font, CommonTexts.loading, this.width / 4, this.height / 4 - 14, -0x1)
                }
                poseStack.popPose()
            } else if (this.files0.isEmpty()) {
                poseStack.pushPose()
                run {
                    poseStack.scale(2f, 2f, 1f)
                    drawCenteredString(
                        poseStack,
                        this.font,
                        CommonTexts.noScreenshots,
                        this.width / 4,
                        this.height / 4,
                        -0x1
                    )
                }
                poseStack.popPose()
            } else {
                poseStack.pushPose()
                run {
                    poseStack.scale(2f, 2f, 1f)
                    drawCenteredString(
                        poseStack,
                        this.font,
                        CommonTexts.errorOccurred,
                        this.width / 4,
                        this.height / 4 - 14,
                        -0x1
                    )
                }
                poseStack.popPose()
                drawCenteredString(
                    poseStack,
                    this.font,
                    CommonTexts.invalidScreenshot,
                    this.width / 2,
                    this.height / 2,
                    -0x1
                )
            }
        }
        poseStack.popPose()
    }

    /**
     * Load texture file into a resource location.
     *
     * @param location the resource location to read the texture into.
     * @param file     the file to read.
     * @return an instance of [DynamicTexture] containing data of the given file.
     */
    fun loadTexture(location: ResourceLocation, file: File): DynamicTexture {
        try {
            FileInputStream(file).use { input ->
                val nativeImage = NativeImage.read(input)
                val texture = DynamicTexture(nativeImage)
                texture.setFilter(true, false)
                val mc = Minecraft.getInstance()
                mc.textureManager.register(location, texture)
                return texture
            }
        } catch (exception: IOException) {
            ScreenshotManagerMod.logger.error("Couldn't read image: {}", file.absolutePath)
            throw exception
        }
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == InputConstants.KEY_LEFT) {
            this.prevShot()
            return true
        }
        if (keyCode == InputConstants.KEY_RIGHT) {
            this.nextShot()
            return true
        }
        return super.keyReleased(keyCode, scanCode, modifiers)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
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
        return super.keyPressed(keyCode, scanCode, modifiers)
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

    override fun onClose() {
        this.back()
    }
}
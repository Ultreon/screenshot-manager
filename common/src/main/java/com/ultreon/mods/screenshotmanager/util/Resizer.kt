package com.ultreon.mods.screenshotmanager.util

class Resizer(srcWidth: Float, srcHeight: Float) {
    val ratio: Float
    var relativeRatio = 0f
    var orientation: Orientation? = null
    val sourceWidth: Float
    val sourceHeight: Float

    init {
        ratio = srcWidth / srcHeight
        if (srcWidth > srcHeight) {
            relativeRatio = srcWidth / srcHeight
            orientation = Orientation.LANDSCAPE
        } else if (srcWidth < srcHeight) {
            relativeRatio = srcHeight / srcWidth
            orientation = Orientation.PORTRAIT
        } else {
            relativeRatio = 1f
            orientation = Orientation.SQUARE
        }
        sourceWidth = srcWidth
        sourceHeight = srcHeight
    }

    fun thumbnail(maxWidth: Float, maxHeight: Float): FloatSize {
        var aspectRatio: Float
        var width: Float
        var height: Float
        if (sourceWidth > sourceHeight) {
            aspectRatio = (sourceWidth / sourceHeight.toDouble()).toFloat()
            width = maxWidth
            height = (width / aspectRatio).toInt().toFloat()
            if (height > maxHeight) {
                aspectRatio = (sourceHeight / sourceWidth.toDouble()).toFloat()
                height = maxHeight
                width = (height / aspectRatio).toInt().toFloat()
            }
        } else {
            aspectRatio = (sourceHeight / sourceWidth.toDouble()).toFloat()
            height = maxHeight
            width = (height / aspectRatio).toInt().toFloat()
            if (width > maxWidth) {
                aspectRatio = (sourceWidth / sourceHeight.toDouble()).toFloat()
                width = maxWidth
                height = (width / aspectRatio).toInt().toFloat()
            }
        }
        return FloatSize(width, height)
    }

    /**
     * Aspect ratio orientation.
     */
    enum class Orientation {
        LANDSCAPE, SQUARE, PORTRAIT
    }
}
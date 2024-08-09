package com.ultreon.mods.screenshotmanager.util

import java.util.*

class FloatSize(var width: Float, var height: Float) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val size = other as FloatSize
        return size.width.compareTo(width) == 0 && size.height.compareTo(height) == 0
    }

    override fun hashCode(): Int {
        return Objects.hash(width, height)
    }
}
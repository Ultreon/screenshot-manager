package com.ultreon.mods.screenshotmanager.util

import java.util.*

class DoubleSize(var width: Double, var height: Double) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as DoubleSize
        return that.width.compareTo(width) == 0 && that.height.compareTo(height) == 0
    }

    override fun hashCode(): Int {
        return Objects.hash(width, height)
    }
}
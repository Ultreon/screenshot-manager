package com.ultreon.mods.screenshotmanager.util

import java.util.*

class IntSize(var width: Int, var height: Int) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val size = other as IntSize
        return width == size.width && height == size.height
    }

    override fun hashCode(): Int {
        return Objects.hash(width, height)
    }
}
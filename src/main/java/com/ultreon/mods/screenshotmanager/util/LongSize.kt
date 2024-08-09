package com.ultreon.mods.screenshotmanager.util

import java.util.*

class LongSize(override var width: Long, override var height: Long) : AbstractSize() {

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val longSize = o as LongSize
        return longSize.width == width && longSize.height == height
    }

    override fun hashCode(): Int {
        return Objects.hash(width, height)
    }
}
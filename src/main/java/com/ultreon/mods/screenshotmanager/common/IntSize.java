package com.ultreon.mods.screenshotmanager.common;

import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public class IntSize {
    public int width;
    public int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntSize size = (IntSize) o;
        return width == size.width && height == size.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }
}

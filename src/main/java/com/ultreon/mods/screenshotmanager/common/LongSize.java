package com.ultreon.mods.screenshotmanager.common;

import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public class LongSize extends AbstractSize {
    public double width;
    public double height;

    public Double getWidth() {
        return width;
    }

    public Double getHeight() {
        return height;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LongSize longSize = (LongSize) o;
        return Double.compare(longSize.width, width) == 0 && Double.compare(longSize.height, height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }
}

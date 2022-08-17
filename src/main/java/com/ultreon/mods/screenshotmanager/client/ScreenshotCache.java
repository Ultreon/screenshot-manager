package com.ultreon.mods.screenshotmanager.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ScreenshotCache {
    private static final ScreenshotCache INSTANCE = new ScreenshotCache();
    private final Map<File, ScreenshotData> cache = new HashMap<>();

    public ScreenshotData cache(File name, DynamicTexture texture) {
        NativeImage pixels = texture.getPixels();
        ScreenshotData data;
        if (pixels == null) {
            data = new ScreenshotData(0, 0);
        } else {
            data = new ScreenshotData(pixels.getWidth(), pixels.getHeight());
        }
        this.cache.put(name, data);

        return data;
    }

    public ScreenshotData get(File file) {
        return cache.get(file);
    }

    public ScreenshotData getOrDefault(File file) {
        return cache.getOrDefault(file, new ScreenshotData(16, 16));
    }

    public static ScreenshotCache get() {
        return INSTANCE;
    }

    public boolean isCached(File file) {
        return cache.containsKey(file);
    }
}

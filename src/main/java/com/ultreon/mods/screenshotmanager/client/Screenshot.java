package com.ultreon.mods.screenshotmanager.client;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public record Screenshot(File file, @Nullable AbstractTexture texture, @Nullable ResourceLocation resourceLocation, ScreenshotData data) {

}

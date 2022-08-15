package com.ultreon.mods.screenshotmanager.text;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class CommonTexts {
    public static final TranslatableComponent PREV = new TranslatableComponent("misc.screenshotmgr.prev");
    public static final TranslatableComponent NEXT = new TranslatableComponent("misc.screenshotmgr.next");
    public static final TranslatableComponent NO_SCREENSHOTS = new TranslatableComponent("screen.screenshotmgr.no_screenshots");
    public static final TranslatableComponent ERROR_OCCURRED = new TranslatableComponent("screen.screenshotmgr.error_occurred");
    public static final TranslatableComponent INVALID_SCREENSHOT = new TranslatableComponent("screen.screenshotmgr.invalid_screenshot");

    public static TranslatableComponent loadedScreenshots(int loaded, int total) {
        return new TranslatableComponent("screen.screenshotmgr.loaded_screenshots", loaded, total);
    }
}

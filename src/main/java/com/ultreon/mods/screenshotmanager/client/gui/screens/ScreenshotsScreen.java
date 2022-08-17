package com.ultreon.mods.screenshotmanager.client.gui.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.guilib.client.gui.screen.FullscreenRenderScreen;
import com.ultreon.mods.guilib.client.gui.widget.ToolbarButton;
import com.ultreon.mods.screenshotmanager.MainMod;
import com.ultreon.mods.screenshotmanager.client.Screenshot;
import com.ultreon.mods.screenshotmanager.client.ScreenshotCache;
import com.ultreon.mods.screenshotmanager.client.ScreenshotData;
import com.ultreon.mods.screenshotmanager.common.FloatSize;
import com.ultreon.mods.screenshotmanager.common.KeyboardHelper;
import com.ultreon.mods.screenshotmanager.common.Resizer;
import com.ultreon.mods.screenshotmanager.text.CommonTexts;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScreenshotsScreen extends FullscreenRenderScreen {
    // No getter / setter.
    private final List<File> files = new ArrayList<>();
    private final List<Screenshot> screenshots = new ArrayList<>();

    // Getter only.
    private final Screen backScreen;
    private Screenshot currentScreenshot;

    // Getter & setter.
    private int index = 0;
    private Thread loadThread;
    private int currentIndex = -1;
    private int total;
    private int loaded;

    private boolean loading = false;

    public ScreenshotsScreen(Component title, Screen backScreen) {
        super(title);
        this.backScreen = backScreen;

        addToolbarItem(new ToolbarButton(0, 0, 50, CommonTexts.PREV, toolbarButton -> prevShot()));
        addToolbarItem(new ToolbarButton(0, 0, 50, CommonTexts.NEXT, toolbarButton -> nextShot()));

        this.reload();
    }

    private void reload() {
        loading = true;
        File dir = new File(Minecraft.getInstance().gameDirectory, "screenshots");
        if (dir.exists()) {
            this.files.addAll(Arrays.asList(Objects.requireNonNull(dir.listFiles())));
        }
        this.total = this.files.size();
        this.index = 0;

        this.screenshots.clear();

        this.loadThread = new Thread(this::loadShots, "ScreenshotLoader");
        this.loadThread.start();
    }

    @SuppressWarnings({"BusyWait", "ConstantConditions"})
    @SneakyThrows
    private void loadShots() {
        this.loaded = 0;

        MainMod.LOGGER.info("Refreshing screenshot cache.");
        AtomicBoolean active = new AtomicBoolean(true);
        for (File file : this.files) {
            active.set(true);
            RenderSystem.recordRenderCall(() -> {
                AbstractTexture texture;
                ScreenshotData data;
                @Nullable ResourceLocation location;

                if (!ScreenshotCache.get().isCached(file)) {
                    location = new ResourceLocation(MainMod.MOD_ID, "screenshots_screen/" + file.getName().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9/._-]", "_"));
                    AbstractTexture tex = Minecraft.getInstance().getTextureManager().getTexture(location, null);

                    if (tex == null) {
                        DynamicTexture dynamicTexture = this.loadTexture(location, file);
                        texture = dynamicTexture;
                        data = ScreenshotCache.get().cache(file, dynamicTexture);

                        if (texture == null) {
                            location = null;
                        }
                    } else {
                        texture = tex;
                        data = ScreenshotCache.get().get(file);
                    }
                } else {
                    data = ScreenshotCache.get().get(file);
                    location = new ResourceLocation(MainMod.MOD_ID, "screenshots_screen/" + file.getName().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9/._-]", "_"));
                    texture = Minecraft.getInstance().getTextureManager().getTexture(location, null);

                }
                Screenshot screenshot = new Screenshot(file, texture, location, data);
                screenshots.add(screenshot);
                this.loaded++;
                active.set(false);
            });

            while (active.get()) {
                Thread.sleep(50);
            }
        }
        while (this.files.size() != this.screenshots.size()) {
            Thread.sleep(50);
        }
        this.loading = false;
        refresh();
//        this.list.loadScreenshots();
    }

    /**
     * Refresh the screenshot cache.
     */
    public void refresh() {
        int selectedIndex = this.index;
        if (selectedIndex < 0) {
            this.index = selectedIndex = 0;
        }
        Screenshot selected;
        if (this.screenshots.isEmpty()) {
            selected = null;
        } else {
            if (selectedIndex >= this.screenshots.size()) {
                this.index = selectedIndex = this.screenshots.size() - 1;
            }
            selected = screenshots.get(selectedIndex);
        }
        int index;
        if (selected == null) {
            this.currentScreenshot = null;
            this.index = -1;
        } else if ((selectedIndex) != currentIndex) {
            index = selectedIndex;
            this.currentIndex = index;
            this.currentScreenshot = screenshots.get(index);
        }
    }

    @SuppressWarnings("resource")
    @Override
    public void renderBackground(@NotNull PoseStack pose) {
        fill(pose, 0, 0, width, height, 0xFF000000);
        if (currentScreenshot != null) {
            @Nullable AbstractTexture texture = currentScreenshot.texture();
            ScreenshotData data = currentScreenshot.data();
            ResourceLocation location = currentScreenshot.resourceLocation();

            if (location == null) {
                location = new ResourceLocation("");
            }

            RenderSystem.setShaderTexture(0, location);

            if (texture != null) {
                int imgWidth = data.width();
                int imgHeight = data.height();

                Resizer resizer = new Resizer(imgWidth, imgHeight);
                FloatSize size = resizer.thumbnail(this.width, this.height);

                int centerX = this.width / 2;
                int centerY = this.height / 2;
                int width = (int) size.width;
                int height = (int) size.height;

                blit(pose,  centerX - width / 2, centerY - height / 2, width, height, 0, 0, imgWidth, imgHeight, imgWidth, imgHeight);
            } else {
                blit(pose,  0, 0, width, height, 0, 0, 16, 16, 16, 16);
            }
        } else if (!this.files.isEmpty() && loading) {
            pose.pushPose();
            {
                pose.scale(2, 2, 1);
                drawCenteredString(pose, font, "Loading...", width / 4, height / 4 - 14, 0xFFFFFFFF);
            }
            pose.popPose();
            drawCenteredString(pose, font, CommonTexts.loadedScreenshots(this.loaded, this.total), width / 2, height / 2, 0xFFFFFFFF);
        } else if (this.files.isEmpty()) {
            pose.pushPose();
            {
                pose.scale(2, 2, 1);
                drawCenteredString(pose, font, CommonTexts.NO_SCREENSHOTS, width / 4, height / 4, 0xFFFFFFFF);
            }
            pose.popPose();
        } else {
            pose.pushPose();
            {
                pose.scale(2, 2, 1);
                drawCenteredString(pose, font, CommonTexts.ERROR_OCCURRED, width / 4, height / 4 - 14, 0xFFFFFFFF);
            }
            pose.popPose();
            drawCenteredString(pose, font, CommonTexts.INVALID_SCREENSHOT, width / 2, height / 2, 0xFFFFFFFF);
        }
    }

    /**
     * Load texture file into a resource location.
     *
     * @param location the resource location to read the texture into.
     * @param file     the file to read.
     * @return an instance of {@linkplain DynamicTexture} containing data of the given file.
     */
    @Nullable
    public DynamicTexture loadTexture(ResourceLocation location, File file) {
        try (InputStream input = new FileInputStream(file)) {
            NativeImage nativeImage = NativeImage.read(input);
            DynamicTexture texture = new DynamicTexture(nativeImage);

            texture.setBlurMipmap(true, false);

            Minecraft mc = Minecraft.getInstance();

            mc.getTextureManager().register(location, texture);
            return texture;
        } catch (Throwable t) {
            MainMod.LOGGER.error("Couldn't read image: {}", file.getAbsolutePath(), t);
            return null;
        }
    }

    /**
     * Go back to previous screen.
     */
    public void back() {
        // Go back to the previous screen.
        Objects.requireNonNull(this.minecraft).setScreen(this.backScreen);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputConstants.KEY_LEFT) {
            prevShot();
            return true;
        }
        if (keyCode == InputConstants.KEY_RIGHT) {
            nextShot();
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (KeyboardHelper.isHoldingCtrl()) {
            if (keyCode == 61 || keyCode == 334) {
                System.out.println("Zooming in...");
                return true;
            }
            if (keyCode == 45 || keyCode == 333) {
                System.out.println("Zooming out...");
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * Go to the previous screenshot.
     */
    public void prevShot() {
        if (this.index > 0) {
            this.index--;
            this.refresh();
        }
    }

    /**
     * Go to the next screenshot.
     */
    public void nextShot() {
        if (this.index < this.files.size() - 1) {
            this.index++;
            this.refresh();
        }
    }

    @Override
    public void onClose() {
        this.back();
    }

    public List<File> getFiles() {
        return Collections.unmodifiableList(this.files);
    }

    @NotNull
    public List<Screenshot> getScreenshots() {
        return Collections.unmodifiableList(this.screenshots);
    }

    public Screen getBackScreen() {
        return backScreen;
    }

    public Screenshot getCurrentScreenshot() {
        return currentScreenshot;
    }

    public int getIndex() {
        return index;
    }

    public Thread getLoadThread() {
        return loadThread;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getTotal() {
        return total;
    }

    public int getLoaded() {
        return loaded;
    }

    public boolean isLoading() {
        return loading;
    }
}

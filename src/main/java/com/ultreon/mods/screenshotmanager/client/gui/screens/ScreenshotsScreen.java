package com.ultreon.mods.screenshotmanager.client.gui.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.ultreon.mods.screenshotmanager.MainMod;
import com.ultreon.mods.screenshotmanager.client.Screenshot;
import com.ultreon.mods.screenshotmanager.client.ScreenshotCache;
import com.ultreon.mods.screenshotmanager.client.ScreenshotData;
import com.ultreon.mods.screenshotmanager.client.gui.widgets.ScreenshotSelectionList;
import com.ultreon.mods.screenshotmanager.common.FloatSize;
import com.ultreon.mods.screenshotmanager.common.KeyboardHelper;
import com.ultreon.mods.screenshotmanager.common.Resizer;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Mod.EventBusSubscriber(modid = MainMod.MOD_ID, value = Dist.CLIENT)
public class ScreenshotsScreen extends AdvancedScreen {
    private static final ResourceLocation WIDGETS = MainMod.res("textures/gui/widgets.png");
    // No getter / setter.
    private final List<File> files = new ArrayList<>();
    private final List<Screenshot> screenshots = new ArrayList<>();

    // Getter only.
    @Getter
    private final Screen backScreen;
    @Getter
    private Screenshot currentScreenshot;

    // Getter & setter.
    @Getter
    private int index;
    @Getter
    private ScreenshotSelectionList list;
    @Getter
    private Thread loadThread;
    @Getter
    private int currentIndex = -1;
    @Getter
    private int total;
    @Getter
    private int loaded;

    /**
     * Screenshots screen: constructor.
     *
     * @param backScreen back screen.
     * @param titleIn    the screen title.
     */
    public ScreenshotsScreen(Screen backScreen, Component titleIn) {
        super(titleIn);
        this.backScreen = backScreen;

        this.reload();
    }

    private void reload() {
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
                @Nullable ResourceLocation location = new ResourceLocation(MainMod.MOD_ID, "screenshots_screen/" + file.getName().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9/._-]", "_"));
                AbstractTexture tex = Minecraft.getInstance().getTextureManager().getTexture(location, null);

                AbstractTexture texture;

                ScreenshotData data;
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
//        this.list.loadScreenshots();
    }

    @Override
    protected void init() {
        super.init();

        this.list = this.addWidget(new ScreenshotSelectionList(this, Minecraft.getInstance(),
                200, this.height - 50, 10, this.height - 40, null));
        this.addRenderableWidget(new Button(10, this.height - 30, 200, 20, CommonComponents.GUI_BACK, (btn) -> this.back()));
    }

    /**
     * Refresh the screenshot cache.
     */
    public void refresh() {
        ScreenshotSelectionList.Entry selected = this.list.getSelected();
        int index;
        if (selected == null) {
            this.currentScreenshot = null;
            this.index = -1;
        } else if ((selected.getIndex()) != currentIndex) {
            index = selected.getIndex();
            this.currentIndex = index;
            this.currentScreenshot = screenshots.get(index);
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void render(@NotNull PoseStack pose, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(pose);

//        this.list.render(pose, mouseX, mouseY, partialTicks);

        // Buffer and tessellator.
//        Tesselator tessellator = Tesselator.getInstance();
//        BufferBuilder bufferbuilder = tessellator.getBuilder();

        // Dirt texture.
        RenderSystem.setShaderTexture(0, BACKGROUND_LOCATION);

        // Color.
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

//        // Render dirt.
//        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
//        bufferbuilder.vertex(0.0D, this.height, 0.0D).uv(0f, (float) this.height / 32f).color(64, 64, 64, 255).endVertex();
//        bufferbuilder.vertex(224, this.height, 0.0D).uv((float) 224 / 32f, (float) this.height / 32f).color(64, 64, 64, 255).endVertex();
//        bufferbuilder.vertex(224, this.height - 40d, 0.0D).uv((float) 224 / 32f, ((float) this.height - 40f) / 32f).color(64, 64, 64, 255).endVertex();
//        bufferbuilder.vertex(0.0D, this.height - 40d, 0.0D).uv(0f, ((float) this.height - 40f) / 32f).color(64, 64, 64, 255).endVertex();
//
//        // Draw
//        tessellator.end();

        // Render all children.
//        for (Widget renderable : this.renderables) {
//            renderable.render(pose, mouseX, mouseY, partialTicks);
//        }

        if (this.loaded != this.total) {
            drawString(pose, font, "Loaded screenshot " + loaded + " of " + total, 20, 20, 0xffffffff);
        } else {

            fill(pose, 220, 10, width, height, 0x7f000000);

            if (currentScreenshot != null) {
                @Nullable AbstractTexture texture = currentScreenshot.texture();
                ScreenshotData data = currentScreenshot.data();
                ResourceLocation location = currentScreenshot.resourceLocation();

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

                    blit(pose, 210 / 2 + centerX - width / 2, centerY - height / 2, 0, 0, width, height, width, height);
                } else {
                    blit(pose, 220, 10, width - 20, height - 20, 0, 0, 2, 2, 2, 2);
                }
            }
        }

        RenderSystem.setShaderTexture(0, WIDGETS);
        blit(pose, width / 2 - 28, height - 32 - 12, 56, 32, 20, 0, 56, 32, 256, 256);

        super.render(pose, mouseX, mouseY, partialTicks);
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
    private void back() {
        // Go back to the previous screen.
        Objects.requireNonNull(this.minecraft).setScreen(this.backScreen);
    }

//    /**
//     * <b>WARNING: Do not invoke!</b>
//     * Input event.
//     */
//    @SubscribeEvent
//    public static void onInput(InputEvent.KeyInputEvent event) {
//        if (event.getAction() != GLFW.GLFW_RELEASE) {
//            return;
//        }
//
//        // Get minecraft instance.
//        Minecraft mc = Minecraft.getInstance();
//
//        // Get current screen.
//        Screen screen = mc.screen;
//
//        // Check if current screen is the screenshots screen.
//        if (screen instanceof ScreenshotsScreen screenshots) {
//            // Navigate
//            if (event.getKey() == 263) screenshots.prevShot();
//            if (event.getKey() == 262) screenshots.nextShot();
//        }
//    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 263) {
            prevShot();
            return true;
        }
        if (keyCode == 262) {
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

    @NonNull
    public List<Screenshot> getScreenshots() {
        return Collections.unmodifiableList(this.screenshots);
    }
}

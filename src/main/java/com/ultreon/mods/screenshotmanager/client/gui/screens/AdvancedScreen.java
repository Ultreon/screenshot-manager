package com.ultreon.mods.screenshotmanager.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.screenshotmanager.client.MCGraphics;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.RenderProperties;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("unused")
public abstract class AdvancedScreen extends Screen {
    protected AdvancedScreen(Component titleIn) {
        super(titleIn);
    }

    public static boolean isPointInRegion(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= (double) (x - 1) && mouseX < (double) (x + width + 1) && mouseY >= (double) (y - 1) && mouseY < (double) (y + height + 1);
    }

    public static boolean isPointInRegion(int x, int y, int width, int height, Point mouse) {
        return mouse.x >= (double) (x - 1) && mouse.x < (double) (x + width + 1) && mouse.y >= (double) (y - 1) && mouse.y < (double) (y + height + 1);
    }

    public <T extends AbstractWidget> T add(T widget) {
        return addRenderableWidget(widget);
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.render(new MCGraphics(matrixStack, font, this), new Point(mouseX, mouseY));
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void fillGradient(@NotNull PoseStack matrixStack, int x1, int y1, int x2, int y2, int colorFrom, int colorTo) {
        super.fillGradient(matrixStack, x1, y1, x2, y2, colorFrom, colorTo);
    }

    @SuppressWarnings("unused")
    protected void render(MCGraphics mcg, Point point) {
        mcg.renderBackground(false);
    }

    public void drawTexture(PoseStack matrixStack, Point pos, Rectangle uv, ResourceLocation resource) {
        this.drawTexture(matrixStack, pos.x, pos.y, uv.x, uv.y, uv.width, uv.height, resource);
    }

    public void drawTexture(PoseStack matrixStack, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight, ResourceLocation resource) {
        RenderSystem.setShaderTexture(0, resource);
        this.blit(matrixStack, x, y, uOffset, vOffset, uWidth, vHeight);
    }

    public void drawTexture(PoseStack matrixStack, int x, int y, int width, int height, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight, ResourceLocation resource) {
        RenderSystem.setShaderTexture(0, resource);
        Screen.blit(matrixStack, x, y, width, height, uOffset, vOffset, uWidth, vHeight, textureWidth, textureHeight);
    }

    public void drawTexture(PoseStack matrixStack, int x, int y, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight, ResourceLocation resource) {
        RenderSystem.setShaderTexture(0, resource);
        Screen.blit(matrixStack, x, y, uOffset, vOffset, uWidth, vHeight, textureWidth, textureHeight);
    }

    public void drawTexture(PoseStack matrixStack, int x, int y, int blitOffset, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight, ResourceLocation resource) {
        RenderSystem.setShaderTexture(0, resource);
        Screen.blit(matrixStack, x, y, blitOffset, uOffset, vOffset, uWidth, vHeight, textureHeight, textureWidth);
    }

    /**
     * Draws an ItemStack.
     * <p>
     * The z index is increased by 32 (and not decreased afterwards), and the item is then rendered at z=200.
     */
    @SuppressWarnings("deprecation")
    public final void drawItemStack(ItemStack stack, int x, int y, String altText) {
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.translate(0.0D, 0.0D, 32.0D);
        RenderSystem.applyModelViewMatrix();
        this.itemRenderer.blitOffset = 200f;
        Font font = RenderProperties.get(stack).getFont(stack);
        if (font == null) font = this.font;
        this.itemRenderer.renderAndDecorateItem(stack, x, y);
        this.itemRenderer.renderGuiItemDecorations(font, stack, x, y, altText);
        this.itemRenderer.blitOffset = 0f;
    }

    @Override
    public final void renderTooltip(@NotNull PoseStack matrixStack, @NotNull ItemStack itemStack, int mouseX, int mouseY) {
        super.renderTooltip(matrixStack, itemStack, mouseX, mouseY);
    }
}

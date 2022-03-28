package com.ultreon.mods.screenshotmanager.mixin;

import com.ultreon.mods.screenshotmanager.MainMod;
import com.ultreon.mods.screenshotmanager.client.gui.screens.ScreenshotsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Component p_96550_) {
        super(p_96550_);
    }

    @Inject(at = @At("RETURN"), method = "init")
    public void init(CallbackInfo ci) {
        int l = this.height / 4 + 48;

        int x = width / 2 - 124;
        int y = l + 24 * 2;

        if (ModList.get().isLoaded("quark")) {
            if (ModList.get().isLoaded("supplementaries")) {
                y -= 24;
            } else {
                x = width / 2 + 104;
            }
        }
        addRenderableWidget(new ImageButton(x, y, 20, 20, 0, 0, MainMod.res("textures/gui/widgets.png"), btn -> {
            if (minecraft == null) {
                this.minecraft = Minecraft.getInstance();
            }
            minecraft.setScreen(new ScreenshotsScreen(this, new TranslatableComponent("screen.screenshots_manager.menu")));
        }));
    }
}

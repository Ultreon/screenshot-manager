package com.ultreon.mods.screenshotmanager.mixin;

import com.ultreon.mods.screenshotmanager.ScreenshotManagerMod;
import dev.ultreon.quantum.client.gui.GuiBuilder;
import dev.ultreon.quantum.client.gui.Screen;
import dev.ultreon.quantum.client.gui.screens.PauseScreen;
import dev.ultreon.quantum.client.gui.screens.TitleScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {
    protected PauseScreenMixin(@Nullable String title) {
        super(title);
    }

    @Inject(method = "build", at = @At(value = "INVOKE", ordinal = 0, target = "Ldev/ultreon/quantum/client/gui/GuiBuilder;add(Ldev/ultreon/quantum/client/gui/widget/Widget;)Ldev/ultreon/quantum/client/gui/widget/Widget;", shift = At.Shift.AFTER))
    private void screenshotMgr$build(GuiBuilder builder, CallbackInfo ci) {
        ScreenshotManagerMod.INSTANCE.addButton(this);
    }
}

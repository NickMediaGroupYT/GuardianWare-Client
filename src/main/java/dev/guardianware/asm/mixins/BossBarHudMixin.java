package dev.guardianware.asm.mixins;

import dev.guardianware.GuardianWare;
import dev.guardianware.client.modules.visuals.NoRender;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(DrawContext context, CallbackInfo info) {
        if (GuardianWare.MODULE_MANAGER.getInstance(NoRender.class).bossBar.getValue()) {
            info.cancel();
        }
    }
}

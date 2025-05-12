package dev.guardianware.asm.mixins;

import dev.guardianware.GuardianWare;
import dev.guardianware.client.events.EventRender2D;
import dev.guardianware.client.modules.client.ModuleHUD;
import dev.guardianware.client.modules.visuals.NoRender;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        EventRender2D event = new EventRender2D(tickCounter.getTickDelta(true), context);
        GuardianWare.EVENT_MANAGER.call(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    private void renderStatusEffectOverlay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo info) {
        if (GuardianWare.MODULE_MANAGER.getInstance(ModuleHUD.class).effectHud.getValue().equals(ModuleHUD.effectHuds.Hide) || GuardianWare.MODULE_MANAGER.getInstance(NoRender.class).statusOverlay.getValue()) {
            info.cancel();
        }
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void renderCrosshair(CallbackInfo ci) {
        if (!GuardianWare.MODULE_MANAGER.isModuleEnabled("Crosshair"))
            return;

        ci.cancel();
    }
}

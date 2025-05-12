package dev.guardianware.asm.mixins;

import dev.guardianware.GuardianWare;
import dev.guardianware.client.modules.client.Font;
import net.minecraft.client.font.Glyph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Glyph.class)
public interface GlyphMixin {
    @Inject(method = "getShadowOffset", at = @At("HEAD"), cancellable = true)
    private void getShadowOffset(CallbackInfoReturnable<Float> info) {
        if (GuardianWare.MODULE_MANAGER != null && GuardianWare.MODULE_MANAGER.getInstance(Font.class).isToggled() && !GuardianWare.MODULE_MANAGER.getInstance(Font.class).textShadow.getValue().equals(Font.ShadowModes.DEFAULT)) {
            info.setReturnValue(GuardianWare.MODULE_MANAGER.getInstance(Font.class).getShadowValue());
        }
    }
}

package dev.guardianware.asm.mixins;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.utilities.IMinecraft;
import dev.guardianware.client.events.EventKey;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin implements IMinecraft {
    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    public void injectOnKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (key != -1) {
            if (mc.currentScreen == null && action == 1) {
                GuardianWare.MODULE_MANAGER.getModules().stream().filter(m -> m.getBind() == key).forEach((a) -> a.toggle(true));
            }
            EventKey event = new EventKey(key, action);
            GuardianWare.EVENT_MANAGER.call(event);
            if (event.isCanceled()) {
                ci.cancel();
            }
        }
    }
}

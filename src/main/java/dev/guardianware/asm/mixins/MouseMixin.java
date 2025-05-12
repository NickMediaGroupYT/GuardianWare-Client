package dev.guardianware.asm.mixins;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.utilities.IMinecraft;
import dev.guardianware.client.events.EventKey;
import net.minecraft.client.Mouse;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin implements IMinecraft {

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    public void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT && button != GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            if (mc.currentScreen == null && action == 1) {
               for (Module m : GuardianWare.MODULE_MANAGER.getModules()) {
                   if (m.getBind() == button) {
                       m.toggle(false);
                   }
               }
            }
            EventKey event = new EventKey(button, action);
            GuardianWare.EVENT_MANAGER.call(event);
            if (event.isCanceled()) {
                ci.cancel();
            }
        }
    }
}

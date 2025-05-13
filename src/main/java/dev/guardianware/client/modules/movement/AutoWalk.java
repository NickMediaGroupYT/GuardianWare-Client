package dev.guardianware.client.modules.movement;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.client.events.EventTick;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.option.KeyBinding;

@RegisterModule(name = "AutoWalk", tag = "AutoWalk", description = "auto walk module for traveling with elytra bounce", category = Module.Category.MOVEMENT)
public class AutoWalk extends Module {

    @Override
    public void onEnable() {
        if (mc.player == null) return;
        KeyBinding.setKeyPressed(mc.options.forwardKey.getDefaultKey(), true); // Simulate forward key down
    }

    @Override
    public void onDisable() {
        KeyBinding.setKeyPressed(mc.options.forwardKey.getDefaultKey(), false); // Release forward key
    }

    @EventHandler
    public void onTick(EventTick event) {
        // Keep pressing forward if still active
        KeyBinding.setKeyPressed(mc.options.forwardKey.getDefaultKey(), true);
    }
}

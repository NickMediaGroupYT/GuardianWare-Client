package dev.guardianware.client.modules.movement;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.client.values.impl.ValueNumber;

@RegisterModule(name="Flight", tag="Flight", description="Allows you to fly.", category= Module.Category.MOVEMENT)
public class ModuleFlight extends Module {
    public ValueNumber speed = new ValueNumber("Speed", "Speed", "", 1, 1, 10);

    @Override
    public void onUpdate() {
        mc.player.getAbilities().flying = true;
        mc.player.getAbilities().setFlySpeed(speed.getValue().floatValue());
    }

    @Override
    public void onDisable() {
        mc.player.getAbilities().flying = false;
    }
}

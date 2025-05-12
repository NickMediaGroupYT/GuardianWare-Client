package dev.guardianware.client.modules.movement;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.Timer;
import dev.guardianware.client.events.EventMotion;
import dev.guardianware.client.values.impl.ValueEnum;

@RegisterModule(name="NoSlow", description="Remove the slow down of certain things.", category=Module.Category.MOVEMENT)
public class NoSlow extends Module {
    private Timer timer = new Timer();
    double velocity;

    public ValueEnum items = new ValueEnum("Items", "Items", "", ItemModes.Off);
    public enum ItemModes {
        Off,
        Normal,
        GrimV3
    }

    @Override
    public void onMotion(EventMotion event) {
        if (mc.player.getVelocity().x > mc.player.getVelocity().z) {
            velocity = mc.player.getVelocity().x;
        } else velocity = mc.player.getVelocity().z;

        if (items.getValue().equals(ItemModes.GrimV3) && mc.player.isUsingItem() && Math.abs(velocity) < 0.05) {
            if (timer.passedDms(10)) {
                mc.player.setVelocity(mc.player.getVelocity().multiply(2.5, 1, 2.5));
                timer.reset();
            }
        }
    }

}

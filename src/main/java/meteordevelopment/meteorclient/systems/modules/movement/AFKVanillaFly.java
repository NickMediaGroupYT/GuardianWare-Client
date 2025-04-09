/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.world.TrailFollower;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.settings.*;

public class AFKVanillaFly extends Module {
    private long lastRocketUse = 0;
    private boolean launched = false;
    private boolean manuallyEnabled = false;
    private double yTarget = -1;

    public AFKVanillaFly() {
        super(Categories.Movement, "AFKVanillaFly", "Maintains a level Y-flight with fireworks and smooth pitch control.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    @Override
    public void onActivate() {
        manuallyEnabled = true; // Track manual activation
        launched = false;
        yTarget = -1;

        Firework firework = Modules.get().get(Firework.class);
        if (!firework.isActive()) firework.toggle();

        if (mc.player == null || !mc.player.isFallFlying()) {
            info("You must be flying before enabling AFKVanillaFly.");
        }
    }

    @Override
    public void onDeactivate() {
        manuallyEnabled = false; // Reset manual flag
        Firework firework = Modules.get().get(Firework.class);
        if (firework.isActive()) firework.toggle();
    }

    public void tickFlyLogic() {
        if (mc.player == null) return;

        double currentY = mc.player.getY();

        if (mc.player.isFallFlying()) {
            if (yTarget == -1 || !launched) {
                yTarget = currentY;
                launched = true;
            }

            double yDiff = currentY - yTarget;

            if (Math.abs(yDiff) > 10.0) {
                yTarget = currentY;
                info("Y-lock reset due to altitude deviation.");
            }

            float targetPitch = 0;
            if (Math.abs(yDiff) > 10.0) {
                targetPitch = (float) (-Math.atan2(yDiff, 100) * (180 / Math.PI));
            } else if (yDiff > 2.0) {
                targetPitch = 10f;
            } else if (yDiff < -2.0) {
                targetPitch = -10f;
            } else {
                targetPitch = 0f;
            }

            // pitch fix
            targetPitch = Math.max(-30f, Math.min(30f, targetPitch));

            float currentPitch = mc.player.getPitch();
            mc.player.setPitch(currentPitch + (targetPitch - currentPitch) * 0.1f);

            Firework firework = Modules.get().get(Firework.class);
            if (!firework.isActive()) firework.toggle();

        } else {
            // Just jumped or crashed out of flight
            TrailFollower trailFollower = Modules.get().get(TrailFollower.class);
            if (trailFollower.isActive() && trailFollower.flightMode.get() == TrailFollower.FlightMode.VANILLA) {
                trailFollower.toggle();
                info("You stopped flying. TrailFollower was disabled.");
            }
            if (!launched) {
                mc.player.jump();
                launched = true;
            }
            yTarget = -1;
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        TrailFollower trailFollower = Modules.get().get(TrailFollower.class);
        Firework firework = Modules.get().get(Firework.class);

        boolean shouldAutoEnable = trailFollower.isActive()
            && trailFollower.flightMode.get() == TrailFollower.FlightMode.VANILLA
            && mc.world != null
            && mc.world.getRegistryKey().getValue().getPath().equals("overworld");
        if (shouldAutoEnable && !this.isActive() && !manuallyEnabled) {
            this.toggle(); // Auto enable
            if (!firework.isActive()) firework.toggle();
        }
        if (!shouldAutoEnable && !manuallyEnabled && this.isActive()) {
            this.toggle(); // Auto disable
            if (firework.isActive()) firework.toggle();
        }
        if (this.isActive()) tickFlyLogic();
    }

    public long getLastRocketUse() {
        return lastRocketUse;
    }

    public void setLastRocketUse(long lastRocketUse) {
        this.lastRocketUse = lastRocketUse;
    }
}

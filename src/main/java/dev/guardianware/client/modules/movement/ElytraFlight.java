package dev.guardianware.client.modules.movement;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.client.events.EventTick;
import dev.guardianware.client.values.impl.ValueBoolean;
import dev.guardianware.client.values.impl.ValueEnum;
import dev.guardianware.client.values.impl.ValueNumber;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@RegisterModule(
        name = "ElytraFlight",
        tag = "ElytraFlight",
        description = "Advanced Elytra flight control with bounce and boost modes.",
        category = Module.Category.MOVEMENT
)
public class ElytraFlight extends Module {

    public enum FlyMode {
        Bounce, Boost, Control
    }

    private final ValueEnum mode = new ValueEnum("Mode", "Mode", "Flight mode behavior", FlyMode.Bounce);
    private final ValueNumber pitchSetting = new ValueNumber("Pitch", "Pitch", "View pitch in Bounce/Boost", 0, -90, 90);
    private final ValueNumber speed = new ValueNumber("Speed", "Speed", "Horizontal speed multiplier", 1, 0, 10);
    private final ValueNumber jumpDelay = new ValueNumber("JumpDelay", "JumpDelay", "Ticks between jumps", 10, 0, 20);
    private final ValueBoolean autoRedeploy = new ValueBoolean("AutoRedeploy", "AutoRedeploy", "Redeply elytra midair", true);

    private int jumpCooldown = 0;

    @Override
    public void onEnable() {
        jumpCooldown = 0;
    }

    @EventHandler
    public void onTick(EventTick event) {
        if (mc.player == null || mc.world == null) return;

        // Ground logic: try jumping and deploying elytra
        if (!mc.player.isFallFlying()) {
            if (mc.player.isOnGround() && jumpCooldown <= 0) {
                mc.player.jump();
                jumpCooldown = jumpDelay.getValue().intValue();
            } else if (mc.player.getVelocity().y < 0) {
                mc.player.networkHandler.sendPacket(
                        new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING)
                );
            }

            if (jumpCooldown > 0) jumpCooldown--;
            return;
        }

        // In-flight behavior by mode
        FlyMode selectedMode = (FlyMode) mode.getValue();

        if (selectedMode != FlyMode.Control) {
            mc.player.setPitch(pitchSetting.getValue().intValue());
        }

        switch (selectedMode) {
            case Bounce -> {
                Vec3d bounceDir = getMovementDirection(speed.getValue().intValue());
                mc.player.setVelocity(bounceDir.x, 0.0, bounceDir.z);

                if (autoRedeploy.getValue() && mc.player.getVelocity().y < -0.08 && mc.player.age % 8 == 0) {
                    mc.player.networkHandler.sendPacket(
                            new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING)
                    );
                }
            }

            case Boost -> {
                Vec3d boostDir = getMovementDirection(speed.getValue().intValue() * 1.5);
                mc.player.addVelocity(boostDir.x, 0.0, boostDir.z);
            }

            case Control -> {
                // Do nothing — native elytra control
            }
        }

        if (jumpCooldown > 0) jumpCooldown--;
    }

    private Vec3d getMovementDirection(double speed) {
        float yawRad = mc.player.getYaw() * 0.017453292F;
        double x = -MathHelper.sin(yawRad) * speed;
        double z = MathHelper.cos(yawRad) * speed;
        return new Vec3d(x, 0.0, z);
    }
}
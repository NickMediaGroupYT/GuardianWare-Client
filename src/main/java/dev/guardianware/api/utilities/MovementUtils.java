package dev.guardianware.api.utilities;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Objects;

public class MovementUtils implements IMinecraft {
    public static boolean isMoving(PlayerEntity player) {
        return player.sidewaysSpeed != 0.0f || player.forwardSpeed != 0.0f;
    }

    public static double getBaseMoveSpeed(double speed) {
        double baseSpeed = speed;
        if (mc.player != null && mc.player.hasStatusEffect(StatusEffects.SPEED)) {
            int amplifier = Objects.requireNonNull(mc.player.getStatusEffect(StatusEffects.SPEED)).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        return baseSpeed;
    }

    public static double getJumpBoost() {
        double defaultSpeed = 0.0;
        if (mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            int amplifier = mc.player.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier();
            defaultSpeed += (double)(amplifier + 1) * 0.1;
        }
        return defaultSpeed;
    }
}

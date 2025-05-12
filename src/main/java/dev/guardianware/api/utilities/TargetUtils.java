package dev.guardianware.api.utilities;

import dev.guardianware.GuardianWare;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;

public class TargetUtils implements IMinecraft {
    public static LivingEntity getTarget(float range, float wallRange, boolean visible, TargetMode targetMode) {
        LivingEntity targetEntity = null;
        for (Entity e : mc.world.getEntities()) {
            LivingEntity entity;
            if (!(e instanceof LivingEntity) || (!mc.player.canSee(entity = (LivingEntity)e) ? !(mc.player.squaredDistanceTo(entity.getX(), entity.getY(), entity.getZ()) <= (double)wallRange) : !(mc.player.squaredDistanceTo(entity.getX(), entity.getY(), entity.getZ()) <= (double)range))) continue;
            if (GuardianWare.FRIEND_MANAGER.isFriend(e.getName().getString()) || entity == mc.player && entity.getName().equals(mc.player.getName()) || !(entity instanceof PlayerEntity) || (entity.isDead() || entity.getHealth() <= 0.0f) && !mc.player.canSee(entity) && visible || ((PlayerEntity)entity).isCreative()) continue;
            if (targetEntity == null) {
                targetEntity = entity;
                continue;
            }
            if (targetMode == TargetMode.Range) {
                if (!(mc.player.squaredDistanceTo(entity.getX(), entity.getY(), entity.getZ()) < mc.player.squaredDistanceTo(targetEntity.getX(), targetEntity.getY(), targetEntity.getZ()))) continue;
                targetEntity = entity;
                continue;
            }
            if (!(entity.getHealth() + entity.getAbsorptionAmount() < targetEntity.getHealth() + targetEntity.getAbsorptionAmount())) continue;
            targetEntity = entity;
        }
        return targetEntity;
    }

    public static PlayerEntity getTarget(float range) {
        PlayerEntity targetPlayer = null;
        for (PlayerEntity player : new ArrayList<>(mc.world.getPlayers())) {
            if (mc.player.squaredDistanceTo(player) > (double)MathUtils.square(range) || player == mc.player || GuardianWare.FRIEND_MANAGER.isFriend(player.getName().getString()) || player.isDead() || player.getHealth() <= 0.0f) continue;
            if (targetPlayer == null) {
                targetPlayer = player;
                continue;
            }
            if (!(mc.player.squaredDistanceTo(player) < mc.player.squaredDistanceTo(targetPlayer))) continue;
            targetPlayer = player;
        }
        return targetPlayer;
    }

    public enum TargetMode {
        Range,
        Health
    }

    public static Box extrapolate(PlayerEntity entity, int ticks) {
        if (entity == null) return null;

        double deltaX = entity.getX() - entity.prevX;
        double deltaZ = entity.getZ() - entity.prevZ;

        double motionX = 0;
        double motionZ = 0;

        for (double i = 1; i <= ticks; i = i + 0.5) {
            if (!mc.world.canCollide(entity, entity.getBoundingBox().offset(new Vec3d(deltaX * i, 0, deltaZ * i)))) {
                motionX = deltaX * i;
                motionZ = deltaZ * i;
            } else {
                break;
            }
        }

        Vec3d vec3d = new Vec3d(motionX, 0, motionZ);
        if (vec3d == null) return null;

        return entity.getBoundingBox().offset(vec3d);
    }

    public static BlockPos getNearestAdjacentBlock(PlayerEntity player) {
        if (player == null || mc.world == null) return null;

        BlockPos origin = player.getBlockPos(); // Player's feet position

        // 4-directional offsets (north, south, west, east)
        BlockPos[] directions = new BlockPos[] {
                origin.north(),
                origin.south(),
                origin.west(),
                origin.east()
        };

        BlockPos nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (BlockPos pos : directions) {
            if (!mc.world.getBlockState(pos).isAir()) {
                double dist = player.getPos().squaredDistanceTo(Vec3d.ofCenter(pos));
                if (dist < minDistance) {
                    minDistance = dist;
                    nearest = pos;
                }
            }
        }

        return nearest;
    }

    public static BlockPos getNearestAdjacentBlock(PlayerEntity player, BlockPos... exclude) {
        BlockPos origin = player.getBlockPos();
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;

        if (BlockUtils.canBreak(origin) && !Arrays.asList(exclude).contains(origin)) {
            double dist = player.squaredDistanceTo(origin.getX() + 0.5, origin.getY() + 0.5, origin.getZ() + 0.5);
            best = origin;
            bestDistance = dist;
        }

        for (Direction dir : Direction.values()) {
            BlockPos candidate = origin.offset(dir);

            if (!BlockUtils.canBreak(candidate)) continue;
            if (Arrays.asList(exclude).contains(candidate)) continue;

            double dist = player.squaredDistanceTo(candidate.getX() + 0.5, candidate.getY() + 0.5, candidate.getZ() + 0.5);
            if (dist < bestDistance) {
                bestDistance = dist;
                best = candidate;
            }
        }

        return best;
    }



}

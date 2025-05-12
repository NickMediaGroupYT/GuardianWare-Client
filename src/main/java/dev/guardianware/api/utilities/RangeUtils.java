package dev.guardianware.api.utilities;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RangeUtils implements IMinecraft {

    public static boolean isInRange(Vec3d pos, double range) {
        return getRange(pos)<range;
    }
    public static boolean isInRange(BlockPos pos, double range) {
        return getRange(pos)<range;
    }
    public static boolean isInRange(Entity entity, double range) {
        return getRange(entity)<range;
    }


    public static double getRange(Vec3d pos) {
        Vec3d playerPos = mc.player.getPos();
        return Math.sqrt(
                Math.pow(Math.abs(pos.x - playerPos.x), 2) +
                        Math.pow(Math.abs(pos.y - playerPos.y), 2) +
                        Math.pow(Math.abs(pos.z - playerPos.z), 2)
        );
    }
    public static double getRange(BlockPos pos) {
        Vec3d playerPos = mc.player.getPos();
        return Math.sqrt(
                Math.pow(Math.abs(pos.getX() - playerPos.x), 2) +
                        Math.pow(Math.abs(pos.getY() - playerPos.y), 2) +
                        Math.pow(Math.abs(pos.getZ() - playerPos.z), 2)
        );
    }
    public static double getRange(Entity entity) {
        Vec3d playerPos = mc.player.getPos();
        Vec3d pos = entity.getPos();
        return Math.sqrt(
                Math.pow(Math.abs(pos.x - playerPos.x), 2) +
                        Math.pow(Math.abs(pos.y - playerPos.y), 2) +
                        Math.pow(Math.abs(pos.z - playerPos.z), 2)
        );
    }


}

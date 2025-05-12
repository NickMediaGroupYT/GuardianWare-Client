package dev.guardianware.api.manager.rotation;

import dev.guardianware.api.utilities.Util;
import dev.guardianware.client.events.EventMotion;
import dev.guardianware.client.events.EventPacketSend;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class RotationManager implements Util {

    private List<Rotation> rotationRequests = new ArrayList<>();
    private float yaw, pitch, currentPriority;

    private void rotateGrim(Rotation rotation) {
        PlayerMoveC2SPacket first = new PlayerMoveC2SPacket.Full(mc.player.getX(),mc.player.getY(),mc.player.getZ(),rotation.getYaw(), rotation.getPitch(), mc.player.isOnGround());
        mc.getNetworkHandler().sendPacket(first);
        yaw = rotation.getYaw();
        pitch = rotation.getPitch();
        yaw = mc.player.getYaw();
        pitch = mc.player.getPitch();
    }

    private void rotate(Rotation rotation) {
        PlayerMoveC2SPacket p = new PlayerMoveC2SPacket.Full(mc.player.getX(),mc.player.getY(),mc.player.getZ(),rotation.getYaw(), rotation.getPitch(), mc.player.isOnGround());
        mc.getNetworkHandler().sendPacket(p);
        yaw = rotation.getYaw();
        pitch = rotation.getPitch();
    }

    public void requestRotation(int priority, float yaw, float pitch, boolean grim) {
        Rotation rotation = new Rotation(priority, yaw, pitch, grim);
        rotationRequests.add(rotation);
    }

    public static float[] getRotationsTo(Vec3d dest) {
        float yaw = (float) (Math.toDegrees(Math.atan2(dest.subtract(mc.player.getPos()).z, dest.subtract(mc.player.getPos()).x)) - 90);
        float pitch = (float) Math.toDegrees(-Math.atan2(dest.subtract(mc.player.getPos()).y, Math.hypot(dest.subtract(mc.player.getPos()).x, dest.subtract(mc.player.getPos()).z)));

        return new float[] {
                MathHelper.wrapDegrees(yaw),
                MathHelper.wrapDegrees(pitch)
        };
    }

    public static float[] getRotationsTo(Entity target) {
        return getRotationsTo(target.getPos());
    }

    public static float[] getRotationsTo(BlockPos pos) {
        return getRotationsTo(Vec3d.ofCenter(pos));
    }

    @EventHandler
    private void OnPacketOutbound(EventPacketSend event) {
        if (mc.player == null || mc.world == null) return;

        for (Rotation rotation : rotationRequests) {
            if (rotation.getPriority() > currentPriority) {
                currentPriority = rotation.getPriority();
                if (rotation.isGrim()) {
                    rotateGrim(rotation);
                } else {
                    rotate(rotation);
                }
                rotationRequests.remove(rotation);
            }
        }

        if (rotationRequests.isEmpty()) {
            currentPriority = 0;
            yaw = -1;
            pitch = -1;
        }

    }

    @EventHandler
    private void onMotion(EventMotion event) {
        if (yaw != -1 && pitch != -1) {
            event.setRotationYaw(yaw);
            event.setRotationPitch(pitch);
        }
    }

}

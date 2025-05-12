package dev.guardianware.api.utilities;

import dev.guardianware.GuardianWare;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

import java.util.ArrayList;
import java.util.List;

public class PlayerUtils implements IMinecraft {
    public static PlayerEntity getTarget(float range) {
        PlayerEntity optimalPlayer = null;
        for (PlayerEntity player : new ArrayList<>(mc.world.getPlayers())) {
            if (!(mc.player.distanceTo(player) <= range) || GuardianWare.FRIEND_MANAGER.isFriend(player.getName().getString()) || player.isDead() || player.getHealth() <= 0.0f && player == mc.player || player.getName().equals(mc.player.getName()) || player.hurtTime != 0) continue;
            if (optimalPlayer == null) {
                optimalPlayer = player;
                continue;
            }
            if (!(mc.player.squaredDistanceTo(player) < mc.player.squaredDistanceTo(optimalPlayer))) continue;
            optimalPlayer = player;
        }
        return optimalPlayer;
    }

    public static List<BlockPos> getSphere(float range, boolean sphere, boolean hollow) {
        ArrayList<BlockPos> blocks = new ArrayList<>();
        int x = mc.player.getBlockPos().getX() - (int)range;
        while ((float)x <= (float)mc.player.getBlockPos().getX() + range) {
            int z = mc.player.getBlockPos().getZ() - (int)range;
            while ((float)z <= (float)mc.player.getBlockPos().getZ() + range) {
                int y;
                int n = y = sphere ? mc.player.getBlockPos().getY() - (int)range : mc.player.getBlockPos().getY();
                while ((float)y < (float)mc.player.getBlockPos().getY() + range) {
                    double distance = (mc.player.getBlockPos().getX() - x) * (mc.player.getBlockPos().getX() - x) + (mc.player.getBlockPos().getZ() - z) * (mc.player.getBlockPos().getZ() - z) + (sphere ? (mc.player.getBlockPos().getY() - y) * (mc.player.getBlockPos().getY() - y) : 0);
                    if (distance < (double)(range * range) && (!hollow || distance >= ((double)range - 1.0) * ((double)range - 1.0))) {
                        blocks.add(new BlockPos(x, y, z));
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return blocks;
    }
    public static GameMode getGameMode(PlayerEntity player) {
        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
        return playerListEntry == null ? GameMode.CREATIVE : playerListEntry.getGameMode();
    }
}

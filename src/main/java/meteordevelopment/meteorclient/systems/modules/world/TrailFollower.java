/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.world;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class TrailFollower extends Module {

    private static final MinecraftClient client = MinecraftClient.getInstance();

    private Set<BlockPos> visitedChunks = new HashSet<>();  // To store visited chunk coordinates

    public TrailFollower() {
        super(Categories.World, "TrailFollower", "Leaves a trail of chunk outlines as you move");
    }

    @Override
    public void onActivate() {
        super.onActivate();
        visitedChunks.clear();  // Clear trail when module is activated
    }

    @Override
    public void onRender() {
        if (client.player == null) return;

        BlockPos playerPos = client.player.getBlockPos();

        // Get the current chunk coordinates based on player's position
        int chunkX = playerPos.getX() >> 4;
        int chunkZ = playerPos.getZ() >> 4;
        BlockPos currentChunk = new BlockPos(chunkX << 4, 0, chunkZ << 4);

        // If the player has entered a new chunk, add it to the visited chunks
        if (!visitedChunks.contains(currentChunk)) {
            visitedChunks.add(currentChunk);
        }

        // Render outlines for all visited chunks
        for (BlockPos chunk : visitedChunks) {
            RenderUtils.renderOutline(chunk, 16, 0, 16, 0, 255, 0); // Green outline for trail
        }
    }
}

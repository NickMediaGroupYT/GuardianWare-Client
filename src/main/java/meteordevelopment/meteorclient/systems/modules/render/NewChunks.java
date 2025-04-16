/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.render;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class NewChunks extends Module {

    private static final MinecraftClient client = MinecraftClient.getInstance();

    public NewChunks() {
        super(Categories.Render, "ChunksModule", "Displays information about loaded chunks");
    }

    @Override
    public void onActivate() {
        super.onActivate();
        // This method runs when the module is activated
    }

    @Override
    public void onDeactivate() {
        super.onDeactivate();
        // This method runs when the module is deactivated
    }

    // Method to get the chunk the player is currently in
    private void printCurrentChunk() {
        ClientPlayerEntity player = client.player;
        if (player != null) {
            BlockPos pos = player.getBlockPos();
            int chunkX = pos.getX() >> 4;  // Shifting by 4 to get the chunk coordinate
            int chunkZ = pos.getZ() >> 4;
            client.player.sendMessage(Text.of("You are in chunk: X: " + chunkX + " Z: " + chunkZ), false);
        }
    }

    // You can render information to the screen, here is an example of how you might do that
    @Override
    public void onRender() {
        ClientPlayerEntity player = client.player;
        if (player != null) {
            printCurrentChunk();
            renderChunkBorders(player.getBlockPos());
        }
    }

    private void renderChunkBorders(BlockPos pos) {
        int chunkX = pos.getX() >> 4;  // Shift by 4 to get chunk coordinates
        int chunkZ = pos.getZ() >> 4;

        // Render a border around the chunk (you could use different render colors and logic)
        RenderUtils.renderOutline(new BlockPos(chunkX << 4, 0, chunkZ << 4), 16, 0, 16, 255, 255, 255);
    }
}


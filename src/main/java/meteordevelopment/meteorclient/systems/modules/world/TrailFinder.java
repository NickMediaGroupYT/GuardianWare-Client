/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.world;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class TrailFinder extends Module {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private Set<BlockPos> visitedChunks = new HashSet<>();  // To store visited chunk coordinates

    // The Webhook URL where we will send the chunk coordinates
    private final String webhookUrl = "YOUR_WEBHOOK_URL";  // Replace with your actual webhook URL

    public TrailFinder() {
        super(Categories.World, "TrailFinder", "Sends visited chunk coordinates to a webhook");
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
            sendChunkToWebhook(currentChunk);  // Send the chunk data to the webhook
        }

        // Render outlines for all visited chunks
        for (BlockPos chunk : visitedChunks) {
            RenderUtils.renderOutline(chunk, 16, 0, 16, 0, 255, 0); // Green outline for trail
        }
    }

    /**
     * Sends the chunk coordinates to the webhook.
     * @param chunk The chunk coordinates to send.
     */
    private void sendChunkToWebhook(BlockPos chunk) {
        // Prepare the JSON payload
        String jsonPayload = String.format("{\"chunk\": {\"x\": %d, \"z\": %d}}", chunk.getX() >> 4, chunk.getZ() >> 4);

        try {
            // Create a URL object for the webhook
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Send the JSON payload to the webhook
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get the response code to verify success or failure
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                // Log the error if the request failed
                client.player.sendMessage(Text.of("Failed to send chunk data to webhook (HTTP " + responseCode + ")"), false);
            } else {
                // Success (optional, you can log a success message here if you want)
                // client.player.sendMessage("Sent chunk data to webhook!", false);
            }

        } catch (Exception e) {
            // Log any exceptions that occur
            e.printStackTrace();
            client.player.sendMessage(Text.of("Failed to send chunk data to webhook"), false);
        }
    }
}

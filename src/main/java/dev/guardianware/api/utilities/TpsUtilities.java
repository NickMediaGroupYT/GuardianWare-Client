package dev.guardianware.api.utilities;

import net.minecraft.client.MinecraftClient;

public class TpsUtilities {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static long lastUpdate = -1;
    private static float currentTPS = 20.0f;

    // Update this every tick (call from onTick)
    public static void update() {
        if (mc.world == null) return;

        long currentTime = System.currentTimeMillis();
        if (lastUpdate != -1) {
            long timeDiff = currentTime - lastUpdate;

            // Rough TPS estimate: 50ms = 20 TPS, 100ms = 10 TPS, etc.
            float tickTime = timeDiff / 50.0f;
            float estimatedTPS = 1000.0f / Math.max(tickTime, 1.0f); // prevent divide by 0

            currentTPS = Math.max(1.0f, Math.min(estimatedTPS, 20.0f));
        }

        lastUpdate = currentTime;
    }

    public static float getTPS() {
        return currentTPS;
    }

    public static long scaleDelay(long baseDelay) {
        float tps = getTPS();

        // Fallback if TPS is near-zero (server freeze)
        if (tps <= 1.5f) {
            return baseDelay * 5; // e.g., TPS=0 -> wait 2.5s if baseDelay=500ms
        }

        float scalingFactor = 20.0f / tps;
        return (long) (baseDelay * scalingFactor);
    }
}

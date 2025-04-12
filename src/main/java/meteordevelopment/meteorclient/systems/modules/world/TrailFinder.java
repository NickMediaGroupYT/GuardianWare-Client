/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.world;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

import static meteordevelopment.meteorclient.utils.misc.PathSeekerUtil.sendWebhook;

public class TrailFinder extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<String> webhookLink = sgGeneral.add(new StringSetting.Builder()
        .name("Webhook Link")
        .description("Your Discord webhook URL.")
        .defaultValue("")
        .build());

    public final Setting<Boolean> ping = sgGeneral.add(new BoolSetting.Builder()
        .name("Ping")
        .description("Whether to ping your Discord ID.")
        .defaultValue(false)
        .build());

    public final Setting<String> discordId = sgGeneral.add(new StringSetting.Builder()
        .name("Discord ID")
        .description("Your Discord user ID.")
        .defaultValue("")
        .visible(ping::get)
        .build());

    // Range settings
    public final Setting<Integer> xPositiveRange = sgGeneral.add(new IntSetting.Builder()
        .name("X+ Range")
        .description("Distance to scan in the +X direction.")
        .defaultValue(150)
        .min(0)
        .max(190)
        .build());

    public final Setting<Integer> xNegativeRange = sgGeneral.add(new IntSetting.Builder()
        .name("X- Range")
        .description("Distance to scan in the -X direction.")
        .defaultValue(150)
        .min(0)
        .max(190)
        .build());

    public final Setting<Boolean> enableZAxis = sgGeneral.add(new BoolSetting.Builder()
        .name("Enable Z Axis")
        .description("Whether to scan Z+ and Z- directions.")
        .defaultValue(false)
        .build());

    public final Setting<Integer> zPositiveRange = sgGeneral.add(new IntSetting.Builder()
        .name("Z+ Range")
        .defaultValue(150)
        .min(0)
        .max(190)
        .visible(enableZAxis::get)
        .build());

    public final Setting<Integer> zNegativeRange = sgGeneral.add(new IntSetting.Builder()
        .name("Z- Range")
        .defaultValue(150)
        .min(0)
        .max(190)
        .visible(enableZAxis::get)
        .build());

    public TrailFinder() {
        super(Categories.World, "TrailFinder", "Notifies you when old chunks are found within a directional range.");
    }

    @Override
    public void onActivate() {
        XaeroPlus.EVENT_BUS.register(this);
    }

    @Override
    public void onDeactivate() {
        XaeroPlus.EVENT_BUS.unregister(this);
    }

    @net.lenni0451.lambdaevents.EventHandler(priority = -1)
    public void onChunkData(ChunkDataEvent event) {
        if (mc.player == null || webhookLink.get().isEmpty()) return;

        int playerX = mc.player.getBlockX();
        int playerZ = mc.player.getBlockZ();
        int chunkX = event.chunk().getPos().x * 16;
        int chunkZ = event.chunk().getPos().z * 16;

        boolean inXRange = (chunkX >= playerX && chunkX <= playerX + xPositiveRange.get())
                        || (chunkX <= playerX && chunkX >= playerX - xNegativeRange.get());

        boolean inZRange = enableZAxis.get() && (
                          (chunkZ >= playerZ && chunkZ <= playerZ + zPositiveRange.get())
                       || (chunkZ <= playerZ && chunkZ >= playerZ - zNegativeRange.get()));

        if (!(inXRange || inZRange)) return;

        boolean isOldChunk = ModuleManager.getModule(OldChunks.class)
            .isOldChunk(event.chunk().getPos().x, event.chunk().getPos().z, event.chunk().getWorld().getRegistryKey());

        if (!isOldChunk) return;

        String discordID = discordId.get().isBlank() ? null : discordId.get();
        String chunkPos = "Chunk at (" + chunkX + ", " + chunkZ + ")";
        new Thread(() -> sendWebhook(
            webhookLink.get(),
            "Old Chunk Detected",
            chunkPos + " near your trail.",
            discordID,
            mc.player.getGameProfile().getName()
        )).start();
    }
}

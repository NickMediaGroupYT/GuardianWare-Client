package dev.guardianware.client.modules.visuals;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.RenderUtils;
import dev.guardianware.client.events.Render3DEvent;
import dev.guardianware.client.values.impl.ValueBoolean;
import dev.guardianware.client.values.impl.ValueCategory;
import dev.guardianware.client.values.impl.ValueColor;
import dev.guardianware.client.values.impl.ValueString;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;

import java.awt.*;

@RegisterModule(name = "ESP", tag = "ESP", description = "", category = Module.Category.VISUALS)
public class ESP extends Module {
    private final ValueBoolean player = new ValueBoolean("Player", "Player", "", false);
    private final ValueBoolean chest = new ValueBoolean("Chest", "Chest", "", false);
    private final ValueBoolean hostileMobs = new ValueBoolean("HostileMobs", "HostileMobs", "", false);
    private final ValueBoolean friendlyMobs = new ValueBoolean("FriendlyMobs", "FriendlyMobs", "", false);
    private final ValueBoolean renderThroughWalls = new ValueBoolean("ThroughWalls", "ThroughWalls", "Render players through walls", true);
    private final ValueColor visibleColor = new ValueColor("VisibleColor", "VisibleColor", "", new Color(0, 255, 0, 80));
    private final ValueColor hiddenColor = new ValueColor("HiddenColor", "HiddenColor", "", new Color(255, 0, 0, 80));

    public void onRender3D(Render3DEvent event) {
        renderPlayer(event);
        renderChest(event);
        renderHostileMobs(event);
        renderFriendlyMobs(event);
    }

    private void renderPlayer(Render3DEvent event) {
        if (player.getValue()) {
            if (mc.world == null || mc.player == null) return;

            for (PlayerEntity player : mc.world.getPlayers()) {
                if (player == mc.player || !(player instanceof AbstractClientPlayerEntity)) continue;

                Box box = player.getBoundingBox().expand(0.1);

                // Render behind walls
                if (renderThroughWalls.getValue()) {
                    RenderUtils.setupRender();
                    RenderUtils.drawBoxFilled(event.getMatrices(), box, hiddenColor.getValue());
                    RenderUtils.endRender();
                }

                // Render on top (visible part)
                RenderUtils.setupRender();
                RenderUtils.drawBoxFilled(event.getMatrices(), box, visibleColor.getValue());
                RenderUtils.endRender();
            }
        } else {
            player.setValue(false);
        }
    }

    private void renderChest(Render3DEvent event) {
        if (chest.getValue()) {  // Assuming chestESP is a toggle setting to enable/disable the ESP
            if (mc.world == null || mc.player == null) return;

            // Loop through all blocks around the player
            int range = 50; // You can adjust this range to scan for containers in a specific area
            for (int x = (int) mc.player.getX() - range; x < mc.player.getX() + range; x++) {
                for (int y = (int) mc.player.getY() - range; y < mc.player.getY() + range; y++) {
                    for (int z = (int) mc.player.getZ() - range; z < mc.player.getZ() + range; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        BlockState state = mc.world.getBlockState(pos);

                        // Check if this block is a container (Chest, Barrel, Shulker Box, etc.)
                        if (state.getBlock() instanceof ChestBlock || state.getBlock() instanceof BarrelBlock || state.getBlock() instanceof EnderChestBlock || state.getBlock() instanceof TrappedChestBlock) {
                            BlockEntity blockEntity = mc.world.getBlockEntity(pos);

                            if (blockEntity != null) {
                                Box box = new Box(pos); // Get the bounding box of the container

                                // Render the bounding box for containers
                                if (renderThroughWalls.getValue()) {
                                    RenderUtils.setupRender();
                                    RenderUtils.drawBoxFilled(event.getMatrices(), box, hiddenColor.getValue()); // Behind walls rendering
                                    RenderUtils.endRender();
                                }

                                // Render on top (visible part)
                                RenderUtils.setupRender();
                                RenderUtils.drawBoxFilled(event.getMatrices(), box, visibleColor.getValue()); // Visible box color
                                RenderUtils.endRender();
                            }
                        }
                    }
                }
            }
        } else {
            chest.setValue(false);
        }
    }

    private void renderHostileMobs(Render3DEvent event) {
        if (hostileMobs.getValue()) {
            if (mc.world == null || mc.player == null) return;

            for (Entity entity : mc.world.getEntities()) {
                if (!(entity instanceof HostileEntity)) continue; // Only hostile mobs

                LivingEntity mob = (LivingEntity) entity;
                Box box = mob.getBoundingBox().expand(0.1);

                if (renderThroughWalls.getValue()) {
                    RenderUtils.setupRender();
                    RenderUtils.drawBoxFilled(event.getMatrices(), box, hiddenColor.getValue());
                    RenderUtils.endRender();
                }

                RenderUtils.setupRender();
                RenderUtils.drawBoxFilled(event.getMatrices(), box, visibleColor.getValue());
                RenderUtils.endRender();
            }
        } else {
            hostileMobs.setValue(false);
        }
    }

    private void renderFriendlyMobs(Render3DEvent event) {
        if (friendlyMobs.getValue()) {
            if (mc.world == null || mc.player == null) return;

            for (Entity entity : mc.world.getEntities()) {
                if (!(entity instanceof LivingEntity)) continue;
                if (entity instanceof HostileEntity) continue; // Skip hostiles
                if (entity == mc.player) continue; // Skip self

                // Optional: refine to only known passive classes like AnimalEntity
                // if (!(entity instanceof AnimalEntity || entity instanceof AmbientEntity)) continue;

                LivingEntity mob = (LivingEntity) entity;
                Box box = mob.getBoundingBox().expand(0.1);

                if (renderThroughWalls.getValue()) {
                    RenderUtils.setupRender();
                    RenderUtils.drawBoxFilled(event.getMatrices(), box, hiddenColor.getValue());
                    RenderUtils.endRender();
                }

                RenderUtils.setupRender();
                RenderUtils.drawBoxFilled(event.getMatrices(), box, visibleColor.getValue());
                RenderUtils.endRender();
            }
        } else {
            friendlyMobs.setValue(false);
        }
    }
}

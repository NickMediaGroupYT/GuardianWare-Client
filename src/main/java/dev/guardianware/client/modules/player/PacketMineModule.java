package dev.guardianware.client.modules.player;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.*;
import dev.guardianware.client.events.Render3DEvent;
import dev.guardianware.client.values.impl.ValueBoolean;
import dev.guardianware.client.values.impl.ValueColor;
import dev.guardianware.client.values.impl.ValueNumber;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.*;
import net.minecraft.block.BlockState;

import java.awt.*;
import java.util.Random;

@RegisterModule(name = "PacketMine", tag = "PacketMine", description = "Packet mine with one click", category = Module.Category.PLAYER)
public class PacketMineModule extends Module {

    private final ValueNumber range = new ValueNumber("Range", "Range", "", 6.0, 1.0, 10.0);
    private final ValueBoolean autoMine = new ValueBoolean("AutoMine", "AutoMine", "Automatically mine blocks in range", true);
    private final ValueColor mineBlockColor = new ValueColor("BlockColor", "BlockColor", "Color of mined block", new Color(0, 255, 0));

    private final MinecraftClient mc = MinecraftClient.getInstance();
    private BlockPos targetBlock = null;
    private long lastMineTime = 0;
    private static final Random RANDOM = new Random();

    @Override
    public void onTick() {
        if (!autoMine.getValue() || mc.player == null || mc.world == null || mc.interactionManager == null) return;

        // Update the TPS value
        TpsUtilities.update();

        // Mine nearby blocks safely with TPS-aware delay
        mineNearbyBlockSafely();
    }

    private void mineNearbyBlockSafely() {
        long baseDelay = 200L; // Base delay at 20 TPS
        long scaledDelay = TpsUtilities.scaleDelay(baseDelay); // TPS-adjusted delay

        // Avoid spamming, respect the delay based on TPS
        if (System.currentTimeMillis() - lastMineTime < scaledDelay) return;

        BlockPos playerPos = mc.player.getBlockPos();
        BlockPos closestTarget = null;

        // Scan blocks within range
        for (int x = -range.getValue().intValue(); x <= range.getValue().intValue(); x++) {
            for (int y = -2; y <= 2; y++) { // Limit vertical range
                for (int z = -range.getValue().intValue(); z <= range.getValue().intValue(); z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (!BlockUtils.canBreak(pos)) continue;

                    double dist = mc.player.squaredDistanceTo(Vec3d.ofCenter(pos));
                    if (dist <= range.getValue().doubleValue()) {
                        closestTarget = pos;
                        break;
                    }
                }
                if (closestTarget != null) break;
            }
            if (closestTarget != null) break;
        }

        if (closestTarget != null) {
            BlockState state = mc.world.getBlockState(closestTarget);
            InventoryUtils.setSlot(InventoryUtils.getBestTool(state));

            // Use packet mining (simulating instant mining)
            mineBlockPacket(closestTarget);
            lastMineTime = System.currentTimeMillis(); // Update the last mine time
        }
    }

    private void mineBlockPacket(BlockPos pos) {
        if (mc.player != null && mc.getNetworkHandler() != null) {
            Direction side = Direction.UP; // The block's direction (top side)

            // Randomized delay to simulate human-like behavior
            long delay = RANDOM.nextInt(100); // Random delay between 0 and 100ms.
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Send the START_DESTROY_BLOCK packet (simulates mining start)
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, side));

            // Immediately send the STOP_DESTROY_BLOCK packet (simulates instant mining with one click)
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, side));
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (targetBlock != null) {
            Box blockBox = new Box(targetBlock).expand(0.5);
            drawBlockBox(event.getMatrices(), blockBox, mineBlockColor.getValue());
        }
    }

    private void drawBlockBox(MatrixStack matrices, Box box, Color color) {
        RenderUtils.setupRender();
        RenderUtils.drawBox(matrices, box, color, 2.0f); // Outline of the box
        RenderUtils.drawBoxFilled(matrices, box, new Color(color.getRed(), color.getGreen(), color.getBlue(), 50)); // Filled box for highlighting
        RenderUtils.endRender();
    }

    public void setTargetBlock(BlockPos pos) {
        targetBlock = pos;
    }
}
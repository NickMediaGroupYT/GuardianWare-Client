package dev.guardianware.client.modules.combat;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.InventoryUtils;
import dev.guardianware.api.utilities.RangeUtils;
import dev.guardianware.api.utilities.RenderUtils;
import dev.guardianware.api.utilities.Timer;
import dev.guardianware.client.events.Render3DEvent;
import dev.guardianware.client.values.impl.ValueBoolean;
import dev.guardianware.client.values.impl.ValueColor;
import dev.guardianware.client.values.impl.ValueNumber;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Box;
import net.minecraft.world.RaycastContext;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RegisterModule(name = "AutoMine", tag = "AutoMine", description = "Automatically mines blocks near enemy players.", category = Module.Category.COMBAT)
public class AutoMineModule extends Module {
    public ValueNumber range = new ValueNumber("Range", "Range", "", 4.5, 1.0, 10.0);
    public ValueBoolean doRender = new ValueBoolean("Render", "Render", "", true);
    public ValueColor fillColor = new ValueColor("Fill", "Fill", "", Color.WHITE);
    public ValueColor outlineColor = new ValueColor("Outline", "Outline", "", Color.RED);
    public ValueNumber lineWidth = new ValueNumber("LineWidth", "LineWidth", "", 1.0, 0.1, 5.0);

    private BlockPos currentTarget = null;
    private final Timer mineTimer = new Timer();
    private final Map<BlockPos, Long> blockCooldowns = new HashMap<>();

    @Override
    public void onTick() {
        if (mc.world == null || mc.player == null) return;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || GuardianWare.FRIEND_MANAGER.isFriend(player.getName().getString())) continue;
            if (!RangeUtils.isInRange(player, range.getValue().doubleValue())) continue;

            BlockPos targetBlock = getAdjacentMineableBlock(player);
            if (targetBlock != null && mineTimer.passedMs(500) && canMineNow(targetBlock)) {
                mineBlock(targetBlock);
                mineTimer.reset();
                break; // Mine only one block per tick
            }
        }
    }

    private BlockPos getAdjacentMineableBlock(PlayerEntity player) {
        BlockPos base = player.getBlockPos();

        for (Direction dir : Direction.values()) {
            BlockPos offset = base.offset(dir);
            if (canBreak(offset)) {
                return offset;
            }
        }

        return null;
    }

    private boolean canBreak(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        Block block = state.getBlock();

        return !state.isAir() && block.getHardness() >= 0 && !state.isLiquid();
    }

    private boolean canMineNow(BlockPos pos) {
        long last = blockCooldowns.getOrDefault(pos, 0L);
        return System.currentTimeMillis() - last > 750; // 750ms cooldown per block
    }

    private void mineBlock(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        int toolSlot = InventoryUtils.getBestTool(state);

        if (toolSlot != mc.player.getInventory().selectedSlot) {
            mc.player.getInventory().selectedSlot = toolSlot;
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(toolSlot));
        }

        Direction direction = getRaycastFacing(pos);
        if (direction == null) direction = Direction.UP; // fallback

        // First dig start packet
        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction));
        mc.player.swingHand(Hand.MAIN_HAND);

        // Delayed second START_DESTROY_BLOCK + swing (Grim bypass)
        Direction finalDirection = direction;
        CompletableFuture.runAsync(() -> {
            try {
                long delay = 40 + new java.util.Random().nextInt(30); // 40-70ms delay
                Thread.sleep(delay);
            } catch (InterruptedException ignored) {}

            mc.execute(() -> {
                mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, finalDirection));
                mc.player.swingHand(Hand.MAIN_HAND);

                // Delayed STOP packet
                CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(50); // 50 ms delay before sending STOP packet
                    } catch (InterruptedException ignored) {}

                    mc.execute(() -> {
                        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, finalDirection));
                    });
                });
            });
        });

        blockCooldowns.put(pos, System.currentTimeMillis());
        currentTarget = pos;
    }

    private Direction getRaycastFacing(BlockPos pos) {
        Vec3d eyePos = mc.player.getEyePos();
        for (Direction dir : Direction.values()) {
            Vec3d offset = Vec3d.ofCenter(pos).add(Vec3d.of(dir.getVector()).multiply(0.5));
            BlockHitResult result = mc.world.raycast(
                    new RaycastContext(eyePos, offset, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player)
            );
            if (result.getBlockPos().equals(pos)) {
                return result.getSide();
            }
        }
        return null;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (!doRender.getValue() || mc.world == null || mc.player == null) return;

        if (currentTarget != null) {
            Box box = new Box(currentTarget);
            RenderUtils.drawBox(event.getMatrices(), currentTarget, outlineColor.getValue(), lineWidth.getValue().doubleValue());
            RenderUtils.drawBoxFilled(event.getMatrices(), box, fillColor.getValue());
        }
    }
}
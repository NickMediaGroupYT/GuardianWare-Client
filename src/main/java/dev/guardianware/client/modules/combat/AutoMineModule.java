package dev.guardianware.client.modules.combat;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.*;
import dev.guardianware.client.events.Render3DEvent;
import dev.guardianware.client.values.impl.ValueBoolean;
import dev.guardianware.client.values.impl.ValueColor;
import dev.guardianware.client.values.impl.ValueNumber;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

@RegisterModule(name="AutoMine", tag="AutoMine", description="Automatically place and break crystals at selected target.", category=Module.Category.COMBAT)
public class AutoMineModule extends Module {
    public ValueNumber range = new ValueNumber("Range", "Range", "", 4.5, 1.0, 10.0);
    public ValueBoolean auto = new ValueBoolean("Auto", "Auto", "", false);
    public ValueBoolean doRender = new ValueBoolean("Render", "Render", "", true);
    public ValueColor fillColor = new ValueColor("Fill", "Fill", "", Color.WHITE);
    public ValueColor outlineColor = new ValueColor("Outline", "Outline", "", Color.WHITE);
    public ValueNumber lineWidth = new ValueNumber("LineWidth", "LineWidth", "", 1.0, 0.1, 5.0);

    BlockPos singlePos;
    private final Timer singleTimer = new Timer();

    @Override
    public void onDisable() {
        if (singlePos != null) {
            sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, singlePos, Direction.UP, sequence()));
        }
        singlePos = null;
    }

    @Override
    public void onTick() {
        if (mc.world == null || mc.player == null) return;

        if (auto.getValue()) {
            for (PlayerEntity player : mc.world.getPlayers()) {
                if (player == mc.player
                        || GuardianWare.FRIEND_MANAGER.isFriend(player.getName().getLiteralString())
                        || !RangeUtils.isInRange(player, range.getValue().doubleValue())) continue;

                BlockPos targetPos = TargetUtils.getNearestAdjacentBlock(player, null, null);
                if (targetPos != null && BlockUtils.canBreak(targetPos)) {
                    if (RangeUtils.isInRange(targetPos, range.getValue().doubleValue())) {
                        mineBlock(targetPos);
                        break;  // Only mine one block per tick to avoid suspicion.
                    }
                }
            }
        }
    }

    private void mineBlock(BlockPos pos) {
        if (singlePos != null && singlePos.equals(pos)) return;  // Don't repeat the same block.

        BlockState state = mc.world.getBlockState(pos);
        int toolSlot = InventoryUtils.getBestTool(state);

        if (toolSlot != mc.player.getInventory().selectedSlot) {
            mc.player.getInventory().selectedSlot = toolSlot;
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(toolSlot));
        }

        singlePos = pos;
        Direction direction = DirectionUtils.getDirection(pos);
        sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction, sequence()));
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (mc.world == null || mc.player == null || !doRender.getValue()) return;

        if (singlePos != null) {
            double centerScale = 0.5;
            double tickProgress = singleTimer.getPassedTicks() + event.getTickDelta();
            double linearProgress = MathHelper.clamp(tickProgress / 1.5, 0.0, 1.0); // Simulated vanilla break time.
            double easedProgress = Math.pow(linearProgress, 2);
            double scale = easedProgress * centerScale;

            Box box = new Box(singlePos).contract(0.5).expand(scale);

            RenderUtils.drawBox(event.getMatrices(), singlePos, outlineColor.getValue(), lineWidth.getValue().doubleValue());
            RenderUtils.drawBoxFilled(event.getMatrices(), box, fillColor.getValue());
        }
    }

    private int sequence() {
        return mc.world.getPendingUpdateManager().incrementSequence().getSequence();
    }
}
package dev.guardianware.client.modules.combat;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.*;
import dev.guardianware.client.events.EventAttackBlock;
import dev.guardianware.client.events.Render3DEvent;
import dev.guardianware.client.values.impl.ValueBoolean;
import dev.guardianware.client.values.impl.ValueColor;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.concurrent.*;

@RegisterModule(name="AutoMine", tag="AutoMine", description="Grim-safe AutoMine using packets", category=Module.Category.COMBAT)
public class AutoMineModule extends Module {

    private final ValueBoolean doubleMine = new ValueBoolean("DoubleMine", "DoubleMine", "Mine 2 blocks at once.", false);
    private final ValueBoolean grimSpeed = new ValueBoolean("GrimSpeed", "GrimSpeed", "use 0.7 mining speed for grim servers", false);
    private final ValueBoolean swing = new ValueBoolean("SwingHand", "SwingHand", "Visually swing hand when done mining block", false);
    public final ValueColor fillColor = new ValueColor("Fill", "Fill", "", Color.WHITE);
    public final ValueColor outlineColor = new ValueColor("Outline", "Outline", "", Color.WHITE);

    private BlockPos singlePos, doublePos = null;
    private double singleBreakingSpeed, doubleBreakingSpeed;
    private Timer singleTimer = new Timer();
    private Timer doubleTimer = new Timer();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> firstTask = null;
    private ScheduledFuture<?> secondTask = null;

    @Override
    public void onAttackBlock(EventAttackBlock event) {
        if (mc.world == null || mc.player == null) return;

        if (BlockUtils.canBreak(event.getPos())) {
            addNext(event.getPos());
        }
        event.cancel(); // Prevent vanilla mining
    }

    @Override
    public void onTick() {
        if (mc.world == null || mc.player == null) return;

        if (singlePos != null && mc.world.getBlockState(singlePos).isAir()) singlePos = null;
        if (doublePos != null && mc.world.getBlockState(doublePos).isAir()) doublePos = null;

        if (mc.options.attackKey.isPressed() && mc.crosshairTarget != null &&
                mc.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK) {
            BlockPos targetPos = ((net.minecraft.util.hit.BlockHitResult) mc.crosshairTarget).getBlockPos();
            if (BlockUtils.canBreak(targetPos)) {
                addNext(targetPos);
            }
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        renderProgress(singlePos, singleTimer, singleBreakingSpeed, event);
        renderProgress(doublePos, doubleTimer, doubleBreakingSpeed, event);
    }

    private void renderProgress(BlockPos pos, Timer timer, double speed, Render3DEvent event) {
        if (pos == null) return;
        double tickProgress = timer.getPassedTicks() + event.getTickDelta();
        double linear = MathHelper.clamp(tickProgress / speed, 0.0, 1.0);
        double eased = Math.pow(linear, 2);
        double scale = eased * 0.5;

        Box box = new Box(pos).contract(0.5).expand(scale);

        RenderUtils.setupRender();
        RenderUtils.drawBox(event.getMatrices(), box, outlineColor.getValue(), 1.0);
        RenderUtils.drawBoxFilled(event.getMatrices(), box, fillColor.getValue());
        RenderUtils.endRender();
    }

    private void mineBlock(BlockPos pos, boolean isDouble) {
        if (mc.player == null || mc.world == null) return;

        int slot = InventoryUtils.getBestTool(mc.world.getBlockState(pos));
        InventoryUtils.setSlot(slot); // Properly sync tool
        InventoryUtils.syncToClient();

        Direction dir = DirectionUtils.getDirection(pos);
        RotationUtils.faceBlock(pos); // Ensure legit facing
        Timer timer = isDouble ? doubleTimer : singleTimer;
        timer.reset();

        double breakSpeed = BlockUtils.getBlockBreakingSpeed(slot, mc.world.getBlockState(pos), mc.player.isOnGround());
        if (grimSpeed.getValue()) breakSpeed *= 0.7;

        sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, dir));

        ScheduledFuture<?> task = scheduler.schedule(() -> {
            sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, dir));
            if (swing.getValue()) mc.player.swingHand(Hand.MAIN_HAND);
            if (isDouble) doublePos = null;
            else singlePos = null;
        }, (long)((1.0 / breakSpeed) * 1000L), TimeUnit.MILLISECONDS);

        if (isDouble) {
            doubleBreakingSpeed = breakSpeed;
            doublePos = pos;
            secondTask = task;
        } else {
            singleBreakingSpeed = breakSpeed;
            singlePos = pos;
            firstTask = task;
        }
    }

    private void addNext(BlockPos pos) {
        if ((singlePos != null && singlePos.equals(pos)) || (doublePos != null && doublePos.equals(pos))) return;
        if (singlePos == null) mineBlock(pos, false);
        else if (doubleMine.getValue() && doublePos == null) mineBlock(pos, true);
    }
}
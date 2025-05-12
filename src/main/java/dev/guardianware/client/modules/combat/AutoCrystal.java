package dev.guardianware.client.modules.combat;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.*;
import dev.guardianware.client.events.EventRender2D;
import dev.guardianware.client.events.Render3DEvent;
import dev.guardianware.client.values.impl.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

@RegisterModule(name="AutoCrystal", tag="AutoCrystal", description="Automatically place and break crystals at selected target.", category=Module.Category.COMBAT)
public class AutoCrystal extends Module {

    // Place
    private final ValueCategory placeCategory = new ValueCategory("Place", "The category for placing crystals.");
    private final ValueNumber placeRange = new ValueNumber("PlaceRange", "PlaceRange", "The range to place crystals at", this.placeCategory, 4.0, 0.0, 6.0);
    private final ValueBoolean placeRotate = new ValueBoolean("Rotate", "Rotate", "",  this.placeCategory,true);
    private final ValueBoolean placeSwing = new ValueBoolean("Swing", "Swing", "", this.placeCategory,true);
    private final ValueEnum swap = new ValueEnum("Swap", "Swap", "method of switching to end crystal", this.placeCategory, SwapModes.Silent);
    private enum SwapModes {
        Silent,
        Normal,
        Off
    }

    // Break
    private final ValueCategory breakCategory = new ValueCategory("Break", "The category for breaking crystals.");
    private final ValueNumber breakRange = new ValueNumber("BreakRange", "BreakRange", "The range to break crystals at", this.breakCategory, 4.0, 0.0, 6.0);
    private final ValueBoolean breakRotate = new ValueBoolean("Rotate", "Rotate", "", this.breakCategory,true);
    private final ValueBoolean breakSwing = new ValueBoolean("Swing", "Swing", "", this.breakCategory,true);

    // Targeting
    private final ValueCategory targetingCategory = new ValueCategory("Targeting", "The category for targeting settings.");
    private final ValueNumber targetRange = new ValueNumber("TargetRange", "TargetRange", "The range to target players at", this.placeCategory, 10.0, 0.0, 20.0);
    private final ValueNumber calculationRange = new ValueNumber("CalculationRange", "Calculation Range", "The range from the target to calculate a position.", this.targetingCategory, 5.0, 1.0, 10.0);
    private final ValueNumber minDmg = new ValueNumber("MinDamage", "MinimumDamage", "The minimum damage to deal to enemy to place a crystal", this.targetingCategory, 4.0, 0.0, 20.0);
    private final ValueNumber maxSelf = new ValueNumber("MaxSelfDamage", "MaxSelfDamage", "The maximum ammount of damage to do to self", this.targetingCategory, 8.0, 0.0, 20.0);
    private final ValueBoolean friends = new ValueBoolean("FriendProtect", "FriendProtect", "Don't Target Friends", this.targetingCategory, true);
    private final ValueBoolean pauseEating = new ValueBoolean("PauseEating", "PauseEating", "pause if player is eating", this.targetingCategory,true);
    private final ValueNumber delay = new ValueNumber("delay", "delay", "", this.targetingCategory, 2,0,20);

    // Render
    private final ValueCategory renderCategory = new ValueCategory("Render", "The category for rendering.");
    private final ValueBoolean renderPos = new ValueBoolean("Render Pos", "RenderPos", "Render the position it will place at", this.renderCategory, true);
    private final ValueColor fillColor = new ValueColor("Fill", "FillColor", "The color that the box will be filled with", this.renderCategory, new Color(163, 66, 253, 102));
    private final ValueColor lineColor = new ValueColor("Line", "LineColor", "The color that the line will be", this.renderCategory, new Color(163, 66, 253, 255));
    private final ValueBoolean damageRender = new ValueBoolean("DamageRender", "DamageRender", "Render damage at the box", this.renderCategory, true);
    private final ValueBoolean textShadow = new ValueBoolean("TextShadow", "TextShadow", "Render damage at the box", this.renderCategory, true);

    PlayerEntity target;
    BlockPos targetPos = null;
    double currentDmg = 0;
    Timer timer = new Timer();
    private int crystalsPlaced = 0;
    private long lastCpsTime = System.currentTimeMillis();
    private int currentCps = 0;

    @Override
    public String getHudInfo() {
        String targetName = target != null ? target.getName().getLiteralString() + ", " : "";
        String dmg = currentDmg != 0 ? currentDmg + ", " : "";
        String cpsStr = currentCps/2+"";
        return targetName + dmg + cpsStr;
    }


    @Override
    public void onRender3D(Render3DEvent event) {
        if (targetPos != null && currentDmg != 0) {
            if (renderPos.getValue()) {
                RenderUtils.drawBox(event.getMatrices(), targetPos, lineColor.getValue(), 1.0);
                RenderUtils.drawBoxFilled(event.getMatrices(), targetPos, fillColor.getValue());
                if (damageRender.getValue()) {
                    RenderUtils.drawTextIn3D(event.getMatrices(), 0, 0, ""+currentDmg, Vec3d.ofCenter(targetPos), Color.WHITE, textShadow.getValue());
                }
            }
        }
    }

    @Override
    public void onRender2D(EventRender2D event) {
        long now = System.currentTimeMillis();
        if (now - lastCpsTime >= 1000) {
            currentCps = crystalsPlaced;
            crystalsPlaced = 0;
            lastCpsTime = now;
        }

        if (!timer.passedDms(delay.getValue().intValue())) return;
        if (mc.player == null || mc.world == null) return;
        if (pauseEating.getValue() && mc.player.isUsingItem()) return;

        int crystalSlot = InventoryUtils.findInHotbar(Items.END_CRYSTAL).slot();
        if (crystalSlot == -1) return;

        PlayerEntity bestTarget = null;
        BlockPos bestPos = null;
        double bestDamage = 0;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || player.isDead()) continue;
            if (distanceTo(player) > targetRange.getValue().doubleValue()) continue;
            if (friends.getValue() && GuardianWare.FRIEND_MANAGER.isFriend(player.getName().getLiteralString())) continue;

            BlockPos pos = calculatePlacePos(player);
            if (pos == null || pos.equals(player.getBlockPos())) continue;
            if (distanceTo(pos) > placeRange.getValue().doubleValue()) continue;

            float dmg = (float) currentDmg;
            if (dmg > bestDamage) {
                bestDamage = dmg;
                bestTarget = player;
                bestPos = pos;
            }
        }
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof EndCrystalEntity && distanceTo(entity.getBlockPos()) < breakRange.getValue().doubleValue()) {
                BlockPos pos = entity.getBlockPos();
                if (breakRotate.getValue()) {
                    float[] rot = RotationUtils.getRotations(pos.getX(), pos.getY(), pos.getZ());
                    //     LootTech.ROTATION.requestRotation(900, rot[0], rot[1], true);
                    RotationUtils.rotateGrim(rot[0], rot[1]);
                }
                mc.interactionManager.attackEntity(mc.player,entity);
                if (breakSwing.getValue()) mc.player.swingHand(Hand.MAIN_HAND);
            }
        }

        // Handle selected target (only 1)
        if (bestTarget != null && bestPos != null) {
            target = bestTarget;
            targetPos = bestPos;
            placeCrystal(bestPos, crystalSlot);
            timer.reset();
        } else {
            // Reset if no valid target
            target = null;
            targetPos = null;
            currentDmg = 0;
        }
    }

    private void placeCrystal(BlockPos pos, int slot) {
        InventoryUtils.syncToClient();
        if (!mc.player.getMainHandStack().isOf(Items.END_CRYSTAL)) {
            switch (swap.getValue().name()) {
                case "Silent" -> InventoryManager.setSlot(slot);
                case "Normal" -> InventoryUtils.switchSlot(slot, false);
            }
        }

        if (placeRotate.getValue()) {
            float[] rot = RotationUtils.getRotations(pos.getX(), pos.getY(), pos.getZ());
            RotationUtils.rotateGrim(rot[0], rot[1]);
        }

        if (placeSwing.getValue()) mc.player.swingHand(Hand.MAIN_HAND);

        BlockHitResult hitResult = new BlockHitResult(Vec3d.ofCenter(pos), DirectionUtils.getDirection(pos), pos, false);
        mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hitResult, 0));

        InventoryManager.syncToClient();
        crystalsPlaced++;
    }


    private void breakCrystalAt(BlockPos pos) {
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof EndCrystalEntity && distanceTo(pos) < breakRange.getValue().doubleValue()) {
                if (breakRotate.getValue()) {
                    float[] rot = RotationUtils.getRotations(pos.getX(), pos.getY(), pos.getZ());
               //     LootTech.ROTATION.requestRotation(900, rot[0], rot[1], true);
                    RotationUtils.rotateGrim(rot[0], rot[1]);
                }
                mc.interactionManager.attackEntity(mc.player,entity);
                if (breakSwing.getValue()) mc.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }

    private BlockPos calculatePlacePos(PlayerEntity player) {
        BlockPos bestPos = null;
        double maxDamage = 0;

        BlockPos playerPos = player.getBlockPos();
        int range = calculationRange.getValue().intValue();

        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (!canPlaceCrystal(pos, player)) continue;

                    float targetDmg = DamageUtils.getCrystalDamage(
                            player,
                            null,
                            pos, null, false
                    );

                    if (targetDmg <= minDmg.getValue().floatValue()) continue;

                    float selfDmg = DamageUtils.getCrystalDamage(mc.player, null, pos, null, false);
                    if (selfDmg > maxSelf.getValue().floatValue()) continue;

                    if (targetDmg > maxDamage) {
                        bestPos = pos;
                        maxDamage = targetDmg;
                        currentDmg = targetDmg;
                    }
                }
            }
        }

        return bestPos;
    }


    @Override
    public void onDisable() {
        InventoryManager.syncToClient();
        InventoryUtils.syncToClient();
    }

    private boolean canPlaceCrystal(BlockPos pos, PlayerEntity entity) {
        var block = mc.world.getBlockState(pos).getBlock();
        if (!(block == net.minecraft.block.Blocks.OBSIDIAN || block == net.minecraft.block.Blocks.BEDROCK)) return false;

        Box entityBox = entity.getBoundingBox();
        Box crystalBox = new Box(pos.up());
        Box selfBox = mc.player.getBoundingBox();
        if (entityBox.intersects(crystalBox) || crystalBox.intersects(selfBox)) return false;

        return mc.world.isAir(pos.up());
    }


    public static double distanceTo(PlayerEntity to) {
        return mc.player.getPos().distanceTo(to.getPos());
    }
    public static double distanceTo(BlockPos to) {
        return mc.player.getPos().distanceTo(Vec3d.of(to));
    }

}

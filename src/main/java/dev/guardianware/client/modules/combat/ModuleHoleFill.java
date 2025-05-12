package dev.guardianware.client.modules.combat;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.*;
import dev.guardianware.client.events.EventMotion;
import dev.guardianware.client.events.Render3DEvent;
import dev.guardianware.client.values.impl.*;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RegisterModule(name="HoleFill", tag="Hole Fill", description="Fills holes so enemies cant go in.", category=Module.Category.COMBAT)
public class ModuleHoleFill extends Module {
    ValueEnum mode = new ValueEnum("Mode", "Mode", "Mode for the holefill.", Modes.Smart);
    ValueEnum autoSwitch = new ValueEnum("Switch", "Switch", "The mode for Switching.", InventoryUtils.SwitchModes.Normal);
    ValueEnum itemSwitch = new ValueEnum("Item", "Item", "The item to place the blocks with.", ItemModes.Obsidian);
    ValueNumber range = new ValueNumber("Range", "Range", "Range to place blocks.", Float.valueOf(8.0f), Float.valueOf(3.0f), Float.valueOf(8.0f));
    ValueNumber targetRange = new ValueNumber("TargetRange", "TargetRange", "The range for the target.", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(10.0f));
    ValueBoolean noTargetDisable = new ValueBoolean("NoTargetDisable", "No Target Check", "Disable the module if there is no target.", false);
    ValueBoolean noHolesDisable = new ValueBoolean("NoHolesDisable", "No Holes Disable", "Disable the module if there are no more holes to fill.", false);
    ValueBoolean nearIgnore = new ValueBoolean("NearIgnore", "Near Ignore", "Ignores the holes that are near you.", false);
    ValueNumber nearRange = new ValueNumber("NearIgnoreRange", "Near Range", "Range to ignore the holes near you.", Float.valueOf(3.0f), Float.valueOf(1.0f), Float.valueOf(5.0f));
    ValueBoolean onlyIfSafe = new ValueBoolean("OnlyIfSafe", "Only If Safe", "Only holefills when you are safe.", false);
    ValueBoolean doubles = new ValueBoolean("DoubleHoles", "Double Holes", "Also fills double holes.", true);
    ValueNumber delay = new ValueNumber("Delay", "Delay", "Delay to fill holes.", 100, 0, 200);
    ValueCategory renderCategory = new ValueCategory("Render", "Render category.");
    ValueBoolean render = new ValueBoolean("FillRender", "Render", "Render the holes you are filling.", this.renderCategory, true);
    ValueNumber width = new ValueNumber("OutlineWidth", "Outline Width", "Outline width of the render.", this.renderCategory, Float.valueOf(1.0f), Float.valueOf(0.5f), Float.valueOf(5.0f));
    ValueColor color = new ValueColor("Color", "Color", "", this.renderCategory, new Color(0, 170, 255, 120));
    private List<BlockPos> placeableHoles = Collections.synchronizedList(new ArrayList<>());
    private PlayerEntity target = null;
    private TimerUtils cooldown = new TimerUtils();
    private BlockPos currentHole = null;
    private Item item = Items.OBSIDIAN;

    @Override
    public void onMotion(EventMotion event) {
        if (itemSwitch.getValue().equals(ItemModes.Chest)) {
            item = Items.ENDER_CHEST;
        }

        if (this.nullCheck()) {
            return;
        }
        this.placeableHoles = this.getHoles();
        this.target = TargetUtils.getTarget(this.targetRange.getValue().floatValue());
        int slot = InventoryUtils.find(item).slot();
        int lastSlot = mc.player.getInventory().selectedSlot;
        if (this.placeableHoles.isEmpty()) {
            this.currentHole = null;
        }
        if (slot == -1) {
            ChatUtils.sendMessage("No blocks could be found.", "HoleFill");
            this.disable(true);
            return;
        }
        if (this.noTargetDisable.getValue() && this.target == null) {
            this.disable(true);
            return;
        }
        if (this.mode.getValue().equals(Modes.Smart) && this.target == null || this.onlyIfSafe.getValue() && !HoleUtils.isInHole(ModuleHoleFill.mc.player)) {
            return;
        }
        if (!this.placeableHoles.isEmpty()) {
            for (BlockPos pos : this.placeableHoles.stream().sorted(Comparator.comparing(p -> mc.player.squaredDistanceTo(Vec3d.of(p)))).toList()) {
                if (!this.cooldown.hasTimeElapsed(this.delay.getValue().intValue())) continue;
                this.currentHole = pos;
                InventoryUtils.switchSlot(slot, this.autoSwitch.getValue().equals(InventoryUtils.SwitchModes.Silent));
                BlockUtils.placeBlock(pos, Hand.MAIN_HAND, true);
                if (!this.autoSwitch.getValue().equals(InventoryUtils.SwitchModes.Strict)) {
                    InventoryUtils.switchSlot(lastSlot, this.autoSwitch.getValue().equals(InventoryUtils.SwitchModes.Silent));
                }
                this.cooldown.reset();
            }
        }
        if (this.noHolesDisable.getValue() && this.placeableHoles.isEmpty()) {
            this.disable(true);
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        super.onRender3D(event);
        if (this.nullCheck()) {
            return;
        }
        if (this.currentHole != null && this.render.getValue()) {
            RenderUtils.drawBlock(RenderUtils.getRenderBB(this.currentHole), this.color.getValue());
            RenderUtils.drawBlockOutline(RenderUtils.getRenderBB(this.currentHole), this.color.getValue(), this.width.getValue().floatValue());
        }
    }

    public List<BlockPos> getHoles() {
        List<BlockPos> holes = Collections.synchronizedList(new ArrayList<>());
        for (BlockPos position : PlayerUtils.getSphere(this.range.getValue().floatValue(), true, false)) {
            if (HoleUtils.isHole(position)) {
                if (this.nearIgnore.getValue() && mc.player.squaredDistanceTo(Vec3d.of(position)) <= (double)MathUtils.square(this.nearRange.getValue().floatValue()) || !BlockUtils.isPositionPlaceable(position, true, true, false)) continue;
                holes.add(position);
                continue;
            }
            if (!HoleUtils.isDoubleHole(position) || !this.doubles.getValue() || ModuleHoleFill.mc.world.getBlockState(position).getBlock() != Blocks.AIR || ModuleHoleFill.mc.world.getBlockState(position.up()).getBlock() != Blocks.AIR || ModuleHoleFill.mc.world.getBlockState(position.up().up()).getBlock() != Blocks.AIR || this.nearIgnore.getValue() && ModuleHoleFill.mc.player.squaredDistanceTo(Vec3d.of(position)) <= (double)MathUtils.square(this.nearRange.getValue().floatValue()) || !BlockUtils.isPositionPlaceable(position, true, true, false)) continue;
            holes.add(position);
        }
        return holes;
    }

    public enum ItemModes {
        Obsidian,
        Chest
    }

    public enum Modes {
        Normal,
        Smart
    }
}
package dev.guardianware.client.modules.combat;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.BlockUtils;
import dev.guardianware.api.utilities.ChatUtils;
import dev.guardianware.api.utilities.InventoryUtils;
import dev.guardianware.api.utilities.MathUtils;
import dev.guardianware.client.events.EventMotion;
import dev.guardianware.client.values.impl.ValueBoolean;
import dev.guardianware.client.values.impl.ValueEnum;
import dev.guardianware.client.values.impl.ValueNumber;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

@RegisterModule(name="Surround", description="Places blocks around your feet to protect you from crystals.", category=Module.Category.COMBAT)
public class ModuleSurround extends Module {
    private final ValueEnum mode = new ValueEnum("Mode", "Mode", "The mode for the Surround.", Modes.Normal);
    private final ValueEnum autoSwitch = new ValueEnum("Switch", "Switch", "The mode for Switching.", InventoryUtils.SwitchModes.Normal);
    private final ValueEnum itemSwitch = new ValueEnum("Item", "Item", "The item to place the blocks with.", ModuleHoleFill.ItemModes.Obsidian);
    private final ValueNumber blocks = new ValueNumber("Blocks", "Blocks", "The amount of blocks that can be placed per tick.", 8, 1, 40);
    private final ValueEnum supports = new ValueEnum("Supports", "Supports", "The support blocks for the Surround.", Supports.Dynamic);
    private final ValueBoolean dynamic = new ValueBoolean("Dynamic", "Dynamic", "Makes the surround place dynamically.", true);
    private final ValueBoolean ignoreCrystals = new ValueBoolean("IgnoreCrystals", "Ignore Crystals", "Ignores crystals when checking if there are any entities in the block that needs to be placed.", false);
    private final ValueBoolean stepDisable = new ValueBoolean("StepDisable", "Step Disable", "Disable if step enabled.", true);
    private final ValueBoolean jumpDisable = new ValueBoolean("JumpDisable", "Jump Disable", "Disable if player jumps.", true);
    private int placements;
    private BlockPos startPosition;
    private Item item = Items.OBSIDIAN;
    private int lastSlot = -1;

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.player == null || mc.world == null) {
            this.disable(false);
            return;
        }
        this.startPosition = new BlockPos((int) Math.round(mc.player.getX()), (int) Math.round(mc.player.getY()), (int) Math.round(mc.player.getZ()));
    }

    @Override
    public void onMotion(EventMotion event) {
        super.onMotion(event);

        // Set item to use for placing
        if (itemSwitch.getValue().equals(ModuleHoleFill.ItemModes.Chest)) {
            item = Items.ENDER_CHEST;
        } else {
            item = Items.OBSIDIAN;
        }

        // Disable if player moved vertically (to prevent buggy behavior)
        if ((double) this.startPosition.getY() != MathUtils.roundToPlaces(mc.player.getY(), 0) && this.mode.getValue().equals(Modes.Normal)) {
            this.disable(true);
            return;
        }

        // Disable if jump or step is enabled
        if (jumpDisable.getValue() && mc.options.jumpKey.isPressed()) {
            this.disable(true);
            return;
        }

        if (stepDisable.getValue() && GuardianWare.MODULE_MANAGER.isModuleEnabled("Step")) {
            this.disable(true);
            return;
        }

        // Find the block to place and ensure it is available in the inventory
        int slot = InventoryUtils.find(item).slot();
        if (slot == -1) {
            ChatUtils.sendMessage("No blocks found for Surround.", "Surround");
            this.disable(true);
            return;
        }

        List<BlockPos> unsafeBlocks = getUnsafeBlocks();
        if (!unsafeBlocks.isEmpty()) {
            boolean silent = autoSwitch.getValue().equals(InventoryUtils.SwitchModes.Silent);

            // Handle silent slot switching
            if (silent) swapToSlot(slot);
            else mc.player.getInventory().selectedSlot = slot;

            for (BlockPos position : unsafeBlocks) {
                if (placements >= blocks.getValue().intValue()) break;

                // Handle support block placement if needed
                boolean needsSupport = BlockUtils.getPlaceableSide(position) == null;
                if (needsSupport && supports.getValue() != Supports.None) {
                    placeBlock(event, position.down()); // place support block
                }

                placeBlock(event, position); // place main block
            }

            // Restore slot after placement
            if (silent) restoreSlot();
        }

        placements = 0;

        // Disable if no unsafe blocks and mode is Toggle
        if (unsafeBlocks.isEmpty() && this.mode.getValue().equals(Modes.Toggle)) {
            this.disable(true);
        }
    }

    public void placeBlock(EventMotion event, BlockPos position) {
        if (placements >= blocks.getValue().intValue()) return;
        if (!BlockUtils.isPositionPlaceable(position, true, true, ignoreCrystals.getValue())) return;

        if (BlockUtils.placeBlock(position, Hand.MAIN_HAND, true)) {
            placements++;
        }
    }

    public List<BlockPos> getUnsafeBlocks() {
        ArrayList<BlockPos> positions = new ArrayList<>();
        for (BlockPos position : this.getOffsets()) {
            if (!mc.world.getBlockState(position).canReplace(new ItemPlacementContext(mc.player, Hand.MAIN_HAND, mc.player.getStackInHand(Hand.MAIN_HAND), new BlockHitResult(Vec3d.of(position), Direction.UP, position, false)))) continue;
            positions.add(position);
        }
        return positions;
    }

    private List<BlockPos> getOffsets() {
        ArrayList<BlockPos> offsets = new ArrayList<>();
        if (this.dynamic.getValue()) {
            // Dynamic surround calculation based on player position and offsets
            offsets.addAll(this.calculateDynamicOffsets());
        } else {
            // Static horizontal surround placement (simpler)
            for (Direction side : Direction.Type.HORIZONTAL) {
                offsets.add(this.getPlayerPosition().add(side.getOffsetX(), 0, side.getOffsetZ()));
            }
        }
        return offsets;
    }

    private List<BlockPos> calculateDynamicOffsets() {
        ArrayList<BlockPos> tempOffsets = new ArrayList<>();
        int xLength = calculateLength(mc.player.getX());
        int zLength = calculateLength(mc.player.getZ());

        // Add positions dynamically around the player
        for (int x = -xLength; x <= xLength; ++x) {
            for (int z = -zLength; z <= zLength; ++z) {
                tempOffsets.add(this.getPlayerPosition().add(x, 0, z));
            }
        }
        return tempOffsets;
    }

    private BlockPos getPlayerPosition() {
        return new BlockPos(mc.player.getBlockX(), (int) mc.player.getY(), mc.player.getBlockZ());
    }

    private int calculateLength(double coord) {
        return (int) (coord - Math.floor(coord));
    }

    private void swapToSlot(int slot) {
        if (mc.player.getInventory().selectedSlot != slot) {
            mc.player.getInventory().selectedSlot = slot;
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
        }
    }

    private void restoreSlot() {
        mc.player.getInventory().selectedSlot = lastSlot;
        mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(lastSlot));
    }

    public enum Supports {
        None,
        Dynamic,
        Static
    }

    public enum Modes {
        Normal,
        Persistent,
        Toggle,
        Shift
    }
}

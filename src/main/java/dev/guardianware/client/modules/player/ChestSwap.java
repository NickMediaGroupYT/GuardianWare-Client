package dev.guardianware.client.modules.player;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.InventoryUtils;
import dev.guardianware.client.values.impl.ValueBoolean;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

@RegisterModule(name="ChestSwap",category= Module.Category.PLAYER,tag="ChestSwap",description = "Swap chestplate with elytra.")
public class ChestSwap extends Module {
    ValueBoolean autoLaunch = new ValueBoolean("AutoLaunch", "AutoLaunch", "Automatically jump and use a rocket (HOTBAR ROCKET ONLY)", false);
    private boolean isHotbar;
    private boolean isElytra;
    @Override
    public void onEnable() {
        if (mc.player == null || mc.player.getInventory() == null) {
            this.disable(true);
            return;
        }
        Item targetItem = determineTargetItem();
        if (targetItem == null) {
            this.disable(true);
            return;
        }
        int targetSlot = locateItemSlot(targetItem);
        if (targetSlot == -1) {
            this.disable(true);
            return;
        }
        if (targetSlot != -1 && targetSlot <= 9) {
            isHotbar = true;
        }

        if (isHotbar) {
            swapItemFromHotbar(targetSlot);
        } else {
            swapItemFromInventory(targetSlot);
        }
        this.disable(true);
    }
    private int locateItemSlot(Item targetItem) {
        int inventorySize = mc.player.getInventory().main.size();
        for (int i = 0; i < inventorySize; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == targetItem) {
                isHotbar = i < 9;
                return i;
            }
        }
        return -1;
    }
    private Item determineTargetItem() {
        Item equipped = mc.player.getInventory().getArmorStack(2).getItem();
        if (equipped == Items.ELYTRA) {
            return Items.NETHERITE_CHESTPLATE;
        }
        if (equipped == Items.NETHERITE_CHESTPLATE) {
            isElytra = true;
            return Items.ELYTRA;
        }
        return null;
    }
    private void swapItemFromHotbar(int slot) {
        InventoryUtils.setSlot(slot);
        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        if (autoLaunch.getValue() && isElytra) {
            mc.player.jump();
            mc.player.startFallFlying();
            InventoryUtils.setSlot(InventoryUtils.findItem(Items.FIREWORK_ROCKET, 0, 9));
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        }
        InventoryUtils.syncToClient();
        isElytra = false;
    }
    private void swapItemFromInventory(int slot) {
        ItemStack equippedItem = mc.player.getInventory().getArmorStack(2);
        InventoryUtils.pickupSlot(slot);

        boolean hasEquippedItem = !equippedItem.isEmpty();
        InventoryUtils.pickupSlot(6);

        if (hasEquippedItem) {
            InventoryUtils.pickupSlot(slot);
        }

        if (autoLaunch.getValue() && isElytra) {
            mc.player.jump();
            mc.player.startFallFlying();
            InventoryUtils.setSlot(InventoryUtils.findItem(Items.FIREWORK_ROCKET, 0, 9));
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        }
        isElytra=false;
    }
}

package dev.guardianware.client.modules.miscellaneous;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.*;
import dev.guardianware.client.events.Render3DEvent;
import dev.guardianware.client.values.impl.ValueBoolean;
import dev.guardianware.client.values.impl.ValueNumber;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

@RegisterModule(name = "HotbarReplenish", tag = "HotbarReplenish", description = "Automatically replenishes items in the hotbar", category = Module.Category.MISCELLANEOUS)
public class HotbarReplenishModule extends Module {

    private final ValueNumber replenishThreshold = new ValueNumber("Replenish Threshold", "ReplenishThreshold", "Threshold for replenishing an item", 5.0, 1.0, 9.0);
    private final ValueNumber replenishDelay = new ValueNumber("Replenish Delay", "ReplenishDelay", "Cooldown between replenishment actions (ms)", 750.0, 500.0, 2000.0);

    private final MinecraftClient mc = MinecraftClient.getInstance();
    private long lastReplenishTime = 0;

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || !canReplenish()) return;

        // Replenish all items in the hotbar
        replenishHotbarItems();
        lastReplenishTime = System.currentTimeMillis();
    }

    private boolean canReplenish() {
        return System.currentTimeMillis() - lastReplenishTime > replenishDelay.getValue().longValue();
    }

    private void replenishHotbarItems() {
        // Loop through each slot in the hotbar
        for (int i = 0; i < 9; i++) {
            // Get the item in the current hotbar slot
            Item itemInHotbar = mc.player.getInventory().getStack(i).getItem();

            // Skip empty slots
            if (itemInHotbar == Items.AIR) continue;

            int currentAmount = mc.player.getInventory().getStack(i).getCount();
            // If the item count is less than the threshold, replenish from the inventory
            if (currentAmount <= replenishThreshold.getValue().intValue()) {
                replenishItemInHotbar(itemInHotbar, i);
            }
        }
    }

    private void replenishItemInHotbar(Item item, int hotbarSlot) {
        int inventorySlot = findItemInInventory(item);
        if (inventorySlot != -1) {
            // Swap the inventory item to the desired hotbar slot
            InventoryUtilities.swapItems(inventorySlot, hotbarSlot);
        }
    }

    private int findItemInInventory(Item item) {
        // Search for the item in the inventory
        for (int i = 9; i < mc.player.getInventory().size(); i++) {
            if (mc.player.getInventory().getStack(i).getItem() == item) {
                return i;
            }
        }
        return -1; // Item not found in the inventory
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        // Optional: Render current hotbar replenishment status
    }
}
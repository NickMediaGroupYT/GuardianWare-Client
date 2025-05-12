package dev.guardianware.api.utilities;

import net.minecraft.client.MinecraftClient;

public class InventoryUtilities {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    // Swaps two slots in the inventory (works for hotbar <-> inventory)
    public static void swapItems(int slot1, int slot2) {
        if (mc.interactionManager == null || mc.player == null) return;

        mc.interactionManager.clickSlot(
                mc.player.playerScreenHandler.syncId, slot1, 0, net.minecraft.screen.slot.SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(
                mc.player.playerScreenHandler.syncId, slot2, 0, net.minecraft.screen.slot.SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(
                mc.player.playerScreenHandler.syncId, slot1, 0, net.minecraft.screen.slot.SlotActionType.PICKUP, mc.player);
    }
}

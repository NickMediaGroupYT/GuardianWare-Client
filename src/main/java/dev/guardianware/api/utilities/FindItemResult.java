package dev.guardianware.api.utilities;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;

public record FindItemResult(int slot, int count) {
    static MinecraftClient mc = MinecraftClient.getInstance();
    public boolean found() {
        return slot != -1;
    }

    public Hand getHand() {
        if (slot == 45) return Hand.OFF_HAND;
        if (slot == mc.player.getInventory().selectedSlot) return Hand.MAIN_HAND;
        return null;
    }

    public boolean isMainHand() {
        return getHand() == Hand.MAIN_HAND;
    }

    public boolean isOffhand() {
        return getHand() == Hand.OFF_HAND;
    }

    public boolean isHotbar() {
        return slot >= 0 && slot <= 8;
    }

    public boolean isMain() {
        return slot >= 9 && slot <= 35;
    }

    public boolean isArmor() {
        return slot >= 36 && slot <= 39;
    }
}

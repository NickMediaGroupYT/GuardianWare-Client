package dev.guardianware.api.utilities;

import com.google.common.collect.Lists;
import dev.guardianware.GuardianWare;
import dev.guardianware.asm.mixins.IClientPlayerInteractionManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class InventoryUtils implements IMinecraft {
    private static final Set<Packet<?>> PACKET_CACHE = new HashSet<>();
    private static int slot;
    private int serverSlot;
    public static int previousSlot = -1;

    public static void switchSlot(int targetSlot, boolean silent) {
        GuardianWare.PLAYER_MANAGER.setSwitching(true);
        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(targetSlot));
        slot=targetSlot;
        syncToClient();
        if (!silent) {
            mc.player.getInventory().selectedSlot = targetSlot;
        }
        GuardianWare.PLAYER_MANAGER.setSwitching(false);
    }

    public static boolean swap(int slot, boolean swapBack) {
        if (slot == 45) return true;
        if (slot < 0 || slot > 8) return false;
        if (swapBack && previousSlot == -1) previousSlot = mc.player.getInventory().selectedSlot;
        else if (!swapBack) previousSlot = -1;

        mc.player.getInventory().selectedSlot = slot;
        ((IClientPlayerInteractionManager) mc.interactionManager).nso$syncSelected();
        return true;
    }

    public static boolean swapBack() {
        if (previousSlot == -1) return false;

        boolean return_ = swap(previousSlot, false);
        previousSlot = -1;
        return return_;
    }

    public static void syncToClient() {
        if (isDesynced()) {
            setSlotForced(mc.player.getInventory().selectedSlot);
        }
    }

    public static void setSlotForced(final int barSlot) {

        final Packet<?> p = new UpdateSelectedSlotC2SPacket(barSlot);
        if (mc.getNetworkHandler() != null) {
            PACKET_CACHE.add(p);
            mc.getNetworkHandler().sendPacket(p);
        }
    }

    public static boolean isDesynced() {
        return mc.player.getInventory().selectedSlot != slot;
    }

    public static int findItem(Item item, int minimum, int maximum) {
        for (int i = minimum; i <= maximum; ++i) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() != item) continue;
            return i;
        }
        return -1;
    }

    public static FindItemResult find(Item... items) {
        return find(itemStack -> {
            for (Item item : items) {
                if (itemStack.getItem() == item) return true;
            }
            return false;
        });
    }

    public static ItemStack get(int slot) {
        if (slot == -2) {
            return mc.player.getInventory().getStack(mc.player.getInventory().selectedSlot);
        }
        return mc.player.getInventory().getStack(slot);
    }

    public static FindItemResult findEmpty() {
        return find(ItemStack::isEmpty);
    }

    public static FindItemResult findInHotbar(Item... items) {
        return findInHotbar(itemStack -> {
            for (Item item : items) {
                if (itemStack.getItem() == item) return true;
            }
            return false;
        });
    }

    public static FindItemResult findInHotbar(Predicate<ItemStack> isGood) {
        if (testInOffHand(isGood)) {
            return new FindItemResult(45, mc.player.getOffHandStack().getCount());
        }

        if (testInMainHand(isGood)) {
            return new FindItemResult(mc.player.getInventory().selectedSlot, mc.player.getMainHandStack().getCount());
        }

        return find(isGood, 0, 8);
    }

    public static FindItemResult find(Predicate<ItemStack> isGood) {
        if (mc.player == null) return new FindItemResult(0, 0);
        return find(isGood, 0, mc.player.getInventory().size());
    }

    public static FindItemResult find(Predicate<ItemStack> isGood, int start, int end) {
        if (mc.player == null) return new FindItemResult(0, 0);

        int slot = -1, count = 0;

        for (int i = start; i <= end; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);

            if (isGood.test(stack)) {
                if (slot == -1) slot = i;
                count += stack.getCount();
            }
        }

        return new FindItemResult(slot, count);
    }

    public static boolean testInMainHand(Predicate<ItemStack> predicate) {
        return predicate.test(mc.player.getMainHandStack());
    }

    public static boolean testInMainHand(Item... items) {
        return testInMainHand(itemStack -> {
            for (var item : items) if (itemStack.isOf(item)) return true;
            return false;
        });
    }

    public static boolean testInOffHand(Predicate<ItemStack> predicate) {
        return predicate.test(mc.player.getOffHandStack());
    }

    public static boolean testInOffHand(Item... items) {
        return testInOffHand(itemStack -> {
            for (var item : items) if (itemStack.isOf(item)) return true;
            return false;
        });
    }

    public static boolean testInHands(Predicate<ItemStack> predicate) {
        return testInMainHand(predicate) || testInOffHand(predicate);
    }

    public static boolean testInHands(Item... items) {
        return testInMainHand(items) || testInOffHand(items);
    }

    public static boolean testInHotbar(Predicate<ItemStack> predicate) {
        if (testInHands(predicate)) return true;

        for (int i = 0; i < 8; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (predicate.test(stack)) return true;
        }

        return false;
    }

    public static boolean testInHotbar(Item... items) {
        return testInHotbar(itemStack -> {
            for (var item : items) if (itemStack.isOf(item)) return true;
            return false;
        });
    }

    public static void offhandItem(Item item) {
        int slot = findItem(item,0, 35);
        if (slot != -1) {
            GuardianWare.PLAYER_MANAGER.setSwitching(true);
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 45, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.tick();
            GuardianWare.PLAYER_MANAGER.setSwitching(false);
        }
    }

    public static boolean isFound(Item item) {
        if (findItem(item, 0, 35) != -1) {
            return true;
        }
        return false;
    }

    public static void setSlot(final int barSlot) {
        if (slot != barSlot && PlayerInventory.isValidHotbarIndex(barSlot)) {
            setSlotForced(barSlot);
        }
    }

    private static void click(int slot, int button, SlotActionType type) {
        ScreenHandler screenHandler = mc.player.currentScreenHandler;
        DefaultedList<Slot> defaultedList = screenHandler.slots;
        int i = defaultedList.size();
        ArrayList<ItemStack> list = Lists.newArrayListWithCapacity(i);
        for (Slot slot1 : defaultedList) {
            list.add(slot1.getStack().copy());
        }
        screenHandler.onSlotClick(slot, button, type, mc.player);
        Int2ObjectOpenHashMap<ItemStack> int2ObjectMap = new Int2ObjectOpenHashMap<>();
        for (int j = 0; j < i; ++j) {
            ItemStack itemStack2;
            ItemStack itemStack = list.get(j);
            if (ItemStack.areEqual(itemStack, itemStack2 = defaultedList.get(j).getStack())) continue;
            int2ObjectMap.put(j, itemStack2.copy());
        }
        mc.player.networkHandler.sendPacket(new ClickSlotC2SPacket(screenHandler.syncId, screenHandler.getRevision(), slot, button, type, screenHandler.getCursorStack().copy(), int2ObjectMap));
    }

    public static void pickupSlot(final int slot) {
        click(slot, 0, SlotActionType.PICKUP);
    }

    public static int getBestTool(BlockState state) {
        int slot = getBestToolNoFallback(state);
        return slot != -1 ? slot : mc.player.getInventory().selectedSlot;
    }

    public static int getBestToolNoFallback(BlockState state) {
        int slot = -1;
        float bestTool = 0.0F;

        for(int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ToolItem) {
                float speed = stack.getMiningSpeedMultiplier(state);
                int efficiency = EnchantmentHelper.getLevel(mc.world.getRegistryManager().get(Enchantments.EFFICIENCY.getRegistryRef()).getEntry(Enchantments.EFFICIENCY).get(), stack);
                if (efficiency > 0) {
                    speed += (float)(efficiency * efficiency) + 1.0F;
                }

                if (speed > bestTool) {
                    bestTool = speed;
                    slot = i;
                }
            }
        }

        return slot;
    }

    public enum SwitchModes {
        Normal,
        Silent,
        Strict
    }

    public static int getEnchantmentLevel(ItemStack itemStack, RegistryKey<Enchantment> enchantment) {
        if (itemStack.isEmpty()) return 0;
        Object2IntMap<RegistryEntry<Enchantment>> itemEnchantments = new Object2IntArrayMap<>();
        getEnchantments(itemStack, itemEnchantments);
        return getEnchantmentLevel(itemEnchantments, enchantment);
    }

    public static int getEnchantmentLevel(Object2IntMap<RegistryEntry<Enchantment>> itemEnchantments, RegistryKey<Enchantment> enchantment) {
        for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : Object2IntMaps.fastIterable(itemEnchantments)) {
            if (entry.getKey().matchesKey(enchantment)) return entry.getIntValue();
        }
        return 0;
    }
    public static void getEnchantments(ItemStack itemStack, Object2IntMap<RegistryEntry<Enchantment>> enchantments) {
        enchantments.clear();

        if (itemStack.isEmpty()) return;

        // Check if it's an enchanted book and use stored enchantments
        if (itemStack.getItem() == Items.ENCHANTED_BOOK && itemStack.contains(DataComponentTypes.STORED_ENCHANTMENTS)) {
            Set<Object2IntMap.Entry<RegistryEntry<Enchantment>>> stored =
                    itemStack.get(DataComponentTypes.STORED_ENCHANTMENTS).getEnchantmentEntries();

            for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : stored) {
                enchantments.put(entry.getKey(), entry.getIntValue());
            }
        }

        // Check for regular enchantments on items (e.g. swords, tools)
        if (itemStack.contains(DataComponentTypes.ENCHANTMENTS)) {
            Set<Object2IntMap.Entry<RegistryEntry<Enchantment>>> regular =
                    itemStack.get(DataComponentTypes.ENCHANTMENTS).getEnchantmentEntries();

            for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : regular) {
                enchantments.put(entry.getKey(), entry.getIntValue());
            }
        }
    }

}

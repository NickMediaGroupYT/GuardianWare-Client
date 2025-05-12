package dev.guardianware.client.modules.combat;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.DamageUtils;
import dev.guardianware.api.utilities.TargetUtils;
import dev.guardianware.api.utilities.TimerUtils;
import dev.guardianware.client.modules.miscellaneous.ModuleMiddleClick;
import dev.guardianware.client.values.impl.ValueBoolean;
import dev.guardianware.client.values.impl.ValueNumber;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.*;
import net.minecraft.screen.slot.SlotActionType;

import java.util.ArrayList;

@RegisterModule(name="AutoArmor", tag="Auto Armor", description="Automatically put armor on.", category=Module.Category.COMBAT)
public class ModuleAutoArmor extends Module {
    ValueNumber delay = new ValueNumber("Delay", "Delay", "Delay to put armor on.", 100, 0, 500);
    ValueBoolean elytraPriority = new ValueBoolean("ElytraPriority", "Elytra Priority", "Prioritize elytra if it is equipped.", false);
    ValueBoolean autoMend = new ValueBoolean("AutoMend", "Auto Mend", "Automatically remove armor when mending.", false);
    ValueNumber armorHP = new ValueNumber("ArmorHeath", "Armor Health", "Health for armor to remove it when mending.", 70.0f, 20.0f, 100.0f);
    ValueNumber enemyRange = new ValueNumber("EnemyRange", "Enemy Range", "Put on armor if enemy is near.", 7.0f, 0.0f, 20.0f);
    ValueNumber pearlRange = new ValueNumber("PearlRange", "Pearl Range", "Put on armor if pear is near.", 7.0f, 0.0f, 20.0f);
    private TimerUtils cooldown = new TimerUtils();

    @Override
    public void onUpdate() {
        boolean allGood;
        super.onUpdate();
        if (nullCheck() || mc.currentScreen instanceof Screen) {
            return;
        }
        ItemStack helmet = mc.player.getInventory().getStack(39);
        ItemStack chestplate = mc.player.getInventory().getStack(38);
        ItemStack pants = mc.player.getInventory().getStack(37);
        ItemStack boots = mc.player.getInventory().getStack(36);
        boolean bl = allGood = (float) DamageUtils.getRoundedDamage(helmet) >= this.armorHP.getValue().floatValue() && (float)DamageUtils.getRoundedDamage(chestplate) >= this.armorHP.getValue().floatValue() && (float)DamageUtils.getRoundedDamage(pants) >= this.armorHP.getValue().floatValue() && (float)DamageUtils.getRoundedDamage(boots) >= this.armorHP.getValue().floatValue();
        if (this.autoMend.getValue() && this.isSafe() && (mc.player.getMainHandStack().getItem().equals(Items.EXPERIENCE_BOTTLE) && mc.options.useKey.isPressed() || ModuleMiddleClick.INSTANCE.xping) && !allGood) {
            this.saveArmor(helmet, 5);
            this.saveArmor(chestplate, 6);
            this.saveArmor(pants, 7);
            this.saveArmor(boots, 8);
        } else {
            this.updateArmor(helmet.getItem(), EquipmentSlot.HEAD, 5);
            if (!this.elytraPriority.getValue() || chestplate.getItem() != Items.ELYTRA) {
                this.updateArmor(chestplate.getItem(), EquipmentSlot.CHEST, 6);
            }
            this.updateArmor(pants.getItem(), EquipmentSlot.LEGS, 7);
            this.updateArmor(boots.getItem(), EquipmentSlot.FEET, 8);
        }
        super.onUpdate();
    }

    private void saveArmor(ItemStack stack, int armorSlot) {
        ArrayList<Integer> emptySlots = this.emptySlots();
        if (!(stack.getItem() == Items.AIR) && (float) DamageUtils.getRoundedDamage(stack) >= this.armorHP.getValue().floatValue() && !emptySlots.isEmpty()) {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, armorSlot, 0, SlotActionType.QUICK_MOVE, mc.player);
        }
    }

    private void updateArmor(Item item, EquipmentSlot type, int newSlot) {
        int newItem;
        if ((item == Items.AIR || !(item instanceof ArmorItem)) && (newItem = this.getArmorSlot(type)) != -1) {
            this.moveItem(newItem, newSlot);
        }
    }

    private void moveItem(int slot, int newSlot) {
        if (this.cooldown.hasTimeElapsed(this.delay.getValue().intValue())) {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot < 9 ? slot + 36 : slot, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, newSlot, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot < 9 ? slot + 36 : slot, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.tick();
            this.cooldown.reset();
        }
    }

    private int getArmorSlot(EquipmentSlot type) {
        for (int i = 0; i < 36; ++i) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!(stack.getItem() instanceof ArmorItem) || !((ArmorItem) stack.getItem()).getSlotType().equals(type)) continue;
            return i;
        }
        return -1;
    }

    private ArrayList<Integer> emptySlots() {
        ArrayList<Integer> emptySlots = new ArrayList<>();
        for (int i = 0; i < 36; ++i) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() != Items.AIR) continue;
            emptySlots.add(i);
        }
        return emptySlots;
    }

    private boolean isSafe() {
        LivingEntity target = TargetUtils.getTarget(enemyRange.getValue().floatValue());
        EnderPearlEntity pearl = getPearl(pearlRange.getValue().floatValue());
        return target == null && pearl == null;
    }

    private EnderPearlEntity getPearl(float range) {
        EnderPearlEntity targetPearl = null;
        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof EnderPearlEntity)) continue;
            EnderPearlEntity pearl = (EnderPearlEntity) e;
            if (!(mc.player.distanceTo(pearl) <= (double) range)) continue;
            if (targetPearl == null) {
                targetPearl = pearl;
                continue;
            }
            if (!(mc.player.distanceTo(pearl) < mc.player.distanceTo(targetPearl))) continue;
            targetPearl = pearl;
        }
        return targetPearl;
    }
}
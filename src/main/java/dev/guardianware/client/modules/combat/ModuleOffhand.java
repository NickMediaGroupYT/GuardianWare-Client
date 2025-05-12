package dev.guardianware.client.modules.combat;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.HoleUtils;
import dev.guardianware.api.utilities.InventoryUtils;
import dev.guardianware.client.events.EventMotion;
import dev.guardianware.client.values.impl.ValueBoolean;
import dev.guardianware.client.values.impl.ValueEnum;
import dev.guardianware.client.values.impl.ValueNumber;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.screen.slot.SlotActionType;

@RegisterModule(name="Offhand", description="Automatically switch items to your offhand.", category=Module.Category.COMBAT)
public class ModuleOffhand extends Module {
    ValueEnum mode = new ValueEnum("Mode", "Mode", "Mode for offhand.", Modes.Totem);
    ValueNumber hp = new ValueNumber("Health", "Health", "Health of player", 12.0f, 1.0f, 20.0f);
    ValueNumber fall = new ValueNumber("Fall", "Fall", "Fall distance.", 10, 5, 30);
    ValueBoolean swordGap = new ValueBoolean("SwordGap", "Sword Gap", "Automatically switch to gap when sword.", false);
    ValueBoolean halfInHole = new ValueBoolean("HalfInHole", "Half In Hole", "require half of hp cfg in hole", true); //TODO: just set this to be a customizeable holehp setting

    @Override
    public void onMotion(EventMotion event) {
        super.onMotion(event);
        if (super.nullCheck()) {
            return;
        }
        if (mc.player.getHealth() + ModuleOffhand.mc.player.getAbsorptionAmount() <= this.hp.getValue().floatValue() || mc.player.fallDistance >= (float)this.fall.getValue().intValue() && !mc.player.isFallFlying() && !HoleUtils.isInHole(mc.player)) {
            if (mc.player.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
                InventoryUtils.offhandItem(Items.TOTEM_OF_UNDYING);
            }
            if (mc.player.getHealth() + mc.player.getAbsorptionAmount() <= hp.getValue().floatValue() / 2 && HoleUtils.isInHole(mc.player) && halfInHole.getValue()) {
                InventoryUtils.offhandItem(Items.TOTEM_OF_UNDYING);
                System.out.println("half hp in hole on"); //remove this after testing
            }
        } else if (mc.player.getOffHandStack().getItem() instanceof SwordItem && this.swordGap.getValue() && mc.mouse.wasRightButtonClicked()) {
            if (mc.player.getOffHandStack().getItem() != Items.GOLDEN_APPLE) {
                InventoryUtils.offhandItem(Items.GOLDEN_APPLE);
            }
        } else if (this.mode.getValue().equals(Modes.Totem) && mc.player.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
            InventoryUtils.offhandItem(Items.TOTEM_OF_UNDYING);
        } else if (this.mode.getValue().equals(Modes.Crystal) && mc.player.getOffHandStack().getItem() != Items.END_CRYSTAL) {
            InventoryUtils.offhandItem(Items.END_CRYSTAL);
        } else if (this.mode.getValue().equals(Modes.Gapple) && mc.player.getOffHandStack().getItem() != Items.GOLDEN_APPLE) {
            InventoryUtils.offhandItem(Items.GOLDEN_APPLE);
        }
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        if (isTotemInOffhand()) return;

        int totemSlot = findTotemSlot();
        if (totemSlot != -1) {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, totemSlot, 40, SlotActionType.SWAP, mc.player);
        }
    }

    @Override
    public String getHudInfo() {
        return this.mode.getValue().name();
    }

    private boolean isTotemInOffhand() {
        return mc.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING;
    }

    private int findTotemSlot() {
        for (int i = 0; i < 36; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                return i < 9 ? i + 36 : i;
            }
        }
        return -1;
    }

    public enum Modes {
        Totem,
        Crystal,
        Gapple
    }
}
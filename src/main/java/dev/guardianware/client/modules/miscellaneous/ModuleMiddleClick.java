package dev.guardianware.client.modules.miscellaneous;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.ChatUtils;
import dev.guardianware.api.utilities.InventoryUtils;
import dev.guardianware.client.modules.client.ModuleCommands;
import dev.guardianware.client.values.impl.ValueBoolean;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

@RegisterModule(name="MiddleClick", tag="Middle Click", description="Add actions to middle click.", category=Module.Category.MISCELLANEOUS)
public class ModuleMiddleClick extends Module {
    public static ModuleMiddleClick INSTANCE;
    ValueBoolean xp = new ValueBoolean("XP", "XP", "" , true);
    ValueBoolean mcf = new ValueBoolean("Friend", "Friend", "" , true);
    ValueBoolean pearl = new ValueBoolean("Pearl", "Pearl", "" , true);
    ValueBoolean rocket = new ValueBoolean("Rocket", "Rocket", "" , true);
    int oldSlot = -1;
    private int delay = 0;
    public boolean xping = false;

    public ModuleMiddleClick() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        this.oldSlot = mc.player.getInventory().selectedSlot;
        int pearlSlot = InventoryUtils.findItem(Items.ENDER_PEARL, 0, 9);
        int rocketSlot = InventoryUtils.findItem(Items.FIREWORK_ROCKET, 0, 9);
        if (xp.getValue()) {
            if (mc.mouse.wasMiddleButtonClicked()) {
                if (this.hotbarXP() != -1) {
                    mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(this.hotbarXP()));
                    mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, mc.player.getInventory().main.get(this.hotbarXP()).getCount(), mc.player.getYaw(), mc.player.getPitch()));
                    mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(this.oldSlot));
                    this.xping = true;
                }
            } else {
                this.xping = false;
            }
        } else if (pearl.getValue() && mc.mouse.wasMiddleButtonClicked() && pearlSlot != -1) {
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(pearlSlot));
            mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, mc.player.getInventory().main.get(pearlSlot).getCount(), mc.player.getYaw(), mc.player.getPitch()));
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(this.oldSlot));
        }
        if (rocket.getValue() && mc.mouse.wasLeftButtonClicked() && rocketSlot != -1) {
            InventoryUtils.setSlot(rocketSlot);
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            InventoryUtils.syncToClient();
        }
        if (mcf.getValue()) {
            Entity entity;
            ++this.delay;
            HitResult object = mc.crosshairTarget;
            if (object == null) {
                return;
            }
            if (object.getType() == HitResult.Type.ENTITY && (entity = ((EntityHitResult) object).getEntity()) instanceof PlayerEntity && !(entity instanceof ArmorStandEntity) && !mc.player.isDead() && mc.player.canSee(entity)) {
                String ID = entity.getName().getString();
                if (mc.mouse.wasMiddleButtonClicked() && mc.currentScreen == null && !GuardianWare.FRIEND_MANAGER.isFriend(ID) && this.delay > 10) {
                    GuardianWare.FRIEND_MANAGER.addFriend(ID);
                    ChatUtils.sendMessage(Formatting.GREEN + "Added " + ModuleCommands.getSecondColor() + ID + ModuleCommands.getFirstColor() + " as friend");
                    this.delay = 0;
                }
                if (mc.mouse.wasMiddleButtonClicked() && mc.currentScreen == null && GuardianWare.FRIEND_MANAGER.isFriend(ID) && this.delay > 10) {
                    GuardianWare.FRIEND_MANAGER.removeFriend(ID);
                    ChatUtils.sendMessage(Formatting.RED + "Removed " + ModuleCommands.getSecondColor() + ID + ModuleCommands.getFirstColor() + " as friend");
                    this.delay = 0;
                }
            }
        }
    }

    private int hotbarXP() {
        for (int i = 0; i < 9; ++i) {
            if (mc.player.getInventory().getStack(i).getItem() != Items.EXPERIENCE_BOTTLE) continue;
            return i;
        }
        return -1;
    }

    public enum modes {
        MCF,
        XP,
        Pearl
    }
}

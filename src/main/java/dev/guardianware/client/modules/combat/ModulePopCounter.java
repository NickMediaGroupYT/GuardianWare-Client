package dev.guardianware.client.modules.combat;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.ChatUtils;
import dev.guardianware.client.events.EventPacketReceive;
import dev.guardianware.client.modules.client.ModuleCommands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

import java.util.HashMap;

@RegisterModule(name="PopCounter", tag="Pop Counter", description="Keeps count of how any totems a player pops.", category=Module.Category.COMBAT)
public class ModulePopCounter extends Module {
    public static final HashMap<String, Integer> popCount = new HashMap<>();

    public void onPacketReceive(EventPacketReceive event) {
        EntityStatusS2CPacket packet;
        if (nullCheck()) {
            return;
        }
        if (event.getPacket() instanceof EntityStatusS2CPacket && (packet = (EntityStatusS2CPacket) event.getPacket()).getStatus() == 35) {
            Entity entity = packet.getEntity(mc.world);
            if (entity == null) {
                return;
            }
            int count = 1;
            if (popCount.containsKey(entity.getName().getString())) {
                count = popCount.get(entity.getName().getString());
                popCount.put(entity.getName().getString(), ++count);
            } else {
                popCount.put(entity.getName().getString(), count);
            }
            if (entity == mc.player) {
                return;
            }
            if (GuardianWare.FRIEND_MANAGER.isFriend(entity.getName().getString())) {
                ChatUtils.sendMessage(ModuleCommands.getFirstColor() + entity.getName().getString() + " popped " + ModuleCommands.getSecondColor() + count + ModuleCommands.getFirstColor() + " totems! you should go help them");
            } else {
                ChatUtils.sendMessage(ModuleCommands.getFirstColor() + entity.getName().getString() + " popped " + ModuleCommands.getSecondColor() + count + ModuleCommands.getFirstColor() + " totems!");
            }
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (nullCheck()) {
            return;
        }
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (!popCount.containsKey(player.getName().getString()) || player.isDead() && !(player.getHealth() <= 0.0f)) continue;
            int count = popCount.get(player.getName().getString());
            popCount.remove(player.getName().getString());
            if (player == mc.player) continue;
            ChatUtils.sendMessage(ModuleCommands.getFirstColor() + player.getName().getString() + " died after popping " + ModuleCommands.getSecondColor() + count + ModuleCommands.getFirstColor() + " totems!");
        }
    }
}
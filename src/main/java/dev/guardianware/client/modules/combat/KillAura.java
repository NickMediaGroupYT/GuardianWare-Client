package dev.guardianware.client.modules.combat;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.client.values.impl.ValueBoolean;
import dev.guardianware.client.values.impl.ValueNumber;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;

@RegisterModule(name = "KillAura", tag = "KillAura", description = "Automatically attacks nearby players when holding a sword.", category = Module.Category.COMBAT)
public class KillAura extends Module {

    public ValueNumber range = new ValueNumber("Range", "Attack Range", "", 4.5, 1.0, 6.0);
    public ValueBoolean playersOnly = new ValueBoolean("PlayersOnly", "Only target players", "", true);
    public ValueBoolean swing = new ValueBoolean("Swing", "Swing Hand", "", true);

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        // Check if holding a sword
        if (!(mc.player.getMainHandStack().getItem() instanceof SwordItem)) return;

        Entity target = null;
        double minDistance = (double) range.getValue();

        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player || !(entity instanceof LivingEntity)) continue;
            if (playersOnly.getValue() && !(entity instanceof PlayerEntity)) continue;
            if (entity.isInvisible()) continue;

            double distance = mc.player.distanceTo(entity);
            if (distance < minDistance) {
                minDistance = distance;
                target = entity;
            }
        }

        if (target != null) {
            if (mc.player.getAttackCooldownProgress(0) >= 1.0f) {
                mc.interactionManager.attackEntity(mc.player, target);
                if (swing.getValue()) {
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
        }
    }
}

package dev.guardianware.client.modules.visuals;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.client.values.impl.ValueBoolean;
import dev.guardianware.client.values.impl.ValueNumber;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;

import java.util.List;

@RegisterModule(name="DeathEffects", tag="DeathEffects", description="Summon lightning at player deaths.", category= Module.Category.VISUALS)
public class DeathEffects extends Module {

    private final ValueBoolean self = new ValueBoolean("Self", "Self", "Summon lightning on self death", true);
    private final ValueBoolean sound = new ValueBoolean("Sound", "Sound", "Play sound for lightning", true);
    private final ValueNumber volume = new ValueNumber("Volume", "Volume", "Volume of lightning sound", 1.0, 0.1, 5.0);

    List<PlayerEntity> lightningList,playerList;

    @Override
    public void onTick() {
        if(mc.player == null || mc.world == null) return;
        for (PlayerEntity player : lightningList) {
            if (!playerList.contains(player)) {
                lightningList.remove(player);
            }
        }
        for (PlayerEntity player : mc.world.getPlayers()) {
            playerList.add(player);
            if (player == null || player.getHealth() > 0.0F || lightningList.contains(player)) continue;
            if (player.getHealth() == 0.0F) {
                if (player == mc.player && self.getValue()) {
                    final LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, mc.world);
                    summonLightning(lightning);
                } else {
                    final LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, mc.world);
                    lightningList.add(player);
                    summonLightning(lightning);
                }
            }
        }
    }

    public void summonLightning(LightningEntity lightning) {
        lightning.updatePosition(lightning.getX(), lightning.getY(), lightning.getZ());
        mc.world.addEntity(lightning);
        if (this.sound.getValue()) {
            mc.player.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, volume.getValue().floatValue(), 1f);
        }
    }

}

package dev.guardianware.client.modules.player;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.InventoryManager;
import dev.guardianware.api.utilities.InventoryUtils;
import dev.guardianware.api.utilities.RotationUtils;
import dev.guardianware.client.values.impl.ValueBoolean;
import dev.guardianware.client.values.impl.ValueNumber;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

@RegisterModule(name = "PearlPhase", tag = "PearlPhase", description = "Phase into a block using a ender pearl.", category = Module.Category.PLAYER)
public class PearlPhase extends Module {
    private final ValueBoolean swing = new ValueBoolean("Swing", "Swing", "", true);
    private final ValueNumber pitch = new ValueNumber("Pitch", "Pitch", "", 86, 70, 90);

    @Override
    public void onEnable() {
        int pearlSlot = getEnderPearlSlot();

            float playerYaw = mc.player.getYaw();
            if (mc.options.backKey.isPressed()) {
                playerYaw = playerYaw-180;
            }
            InventoryManager.setSlot(pearlSlot);
           // LootTech.ROTATION.requestRotation(1000, playerYaw, pitch.getValue().floatValue(), true);
            RotationUtils.rotateGrim(playerYaw, (float) pitch.getValue().doubleValue());
            mc.getNetworkHandler().sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND,1,playerYaw, pitch.getValue().floatValue()));
            InventoryManager.syncToClient();
            if (swing.getValue()) {
                mc.player.swingHand(Hand.MAIN_HAND);
            }
            this.disable(false);
    }

    private int getEnderPearlSlot() {
       return InventoryUtils.find(Items.ENDER_PEARL).slot();
    }

}

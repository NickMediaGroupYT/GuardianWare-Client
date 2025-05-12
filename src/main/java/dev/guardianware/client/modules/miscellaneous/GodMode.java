package dev.guardianware.client.modules.miscellaneous;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.client.events.EventPacketSend;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;

@RegisterModule(name="GodMode2b2t", tag="GodMode2b2t", description="makes you invincible after you die on 2b2t", category= Module.Category.MISCELLANEOUS)
public class GodMode extends Module {

    @Override
    public void onPacketSend(EventPacketSend event) {
        if (event.getPacket() instanceof TeleportConfirmC2SPacket) {
            event.cancel();
        }
    }

}

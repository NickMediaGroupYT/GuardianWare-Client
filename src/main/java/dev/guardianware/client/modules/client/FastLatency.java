package dev.guardianware.client.modules.client;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.client.events.EventPacketReceive;
import dev.guardianware.client.events.Render3DEvent;
import dev.guardianware.api.utilities.Timer;
import dev.guardianware.client.values.impl.ValueNumber;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;


@RegisterModule(name = "FastLatency", category = Module.Category.CLIENT, tag = "FastLatency")
public class FastLatency extends Module {

    public static ValueNumber delay = new ValueNumber("Delay", "Delay", "", 80, 0, 1000);

    private final Timer timer = new Timer();
    private final Timer limitTimer = new Timer();
    private long ping;
    public int resolvedPing;
    private static FastLatency INSTANCE = new FastLatency();
    public static FastLatency getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FastLatency();
        }
        return INSTANCE;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (timer.passedMs((int)delay.getValue())) {
            sendPacket(new RequestCommandCompletionsC2SPacket(134414, "w "));
            ping = System.currentTimeMillis();
            timer.reset();
        }
    }

    @Override
    public void onPacketReceive(EventPacketReceive event) {
        if (event.getPacket() instanceof CommandSuggestionsS2CPacket c && c.id() == 134414) {
            resolvedPing = Math.clamp(System.currentTimeMillis() - ping, 0, 1000);
            timer.setMs(5000);
        }
    }

}

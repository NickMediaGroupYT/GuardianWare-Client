package dev.guardianware.api.utilities;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.event.EventListener;
import dev.guardianware.client.events.EventPacketReceive;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;

public class TPSUtils implements EventListener {
    private static final float[] tickRates = new float[20];
    private int nextIndex = 0;
    private long timeLastTimeUpdate = -1L;

    public TPSUtils() {
        Arrays.fill(tickRates, 0.0f);
        GuardianWare.EVENT_MANAGER.register(this);
    }

    public static float getTickRate() {
        float numTicks = 0.0f;
        float sumTickRates = 0.0f;
        for (float tickRate : tickRates) {
            if (!(tickRate > 0.0f)) continue;
            sumTickRates += tickRate;
            numTicks += 1.0f;
        }
        return MathHelper.clamp(sumTickRates / numTicks, 0.0f, 20.0f);
    }

    public static float getTpsFactor() {
        float TPS = getTickRate();
        return 20.0f / TPS;
    }

    private void onTimeUpdate() {
        if (this.timeLastTimeUpdate != -1L) {
            float timeElapsed = (float) (System.currentTimeMillis() - this.timeLastTimeUpdate) / 1000.0f;
            tickRates[this.nextIndex % TPSUtils.tickRates.length] = MathHelper.clamp(20.0f / timeElapsed, 0.0f, 20.0f);
            ++this.nextIndex;
        }
        this.timeLastTimeUpdate = System.currentTimeMillis();
    }

    @Override
    public void onPacketReceive(EventPacketReceive event) {
        if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
            this.onTimeUpdate();
        }
    }
}

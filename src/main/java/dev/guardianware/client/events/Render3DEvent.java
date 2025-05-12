package dev.guardianware.client.events;

import dev.guardianware.api.manager.event.EventArgument;
import dev.guardianware.api.manager.event.EventListener;
import net.minecraft.client.util.math.MatrixStack;

public class Render3DEvent extends EventArgument {
    private final float tick;
    private final MatrixStack matrices;

    public Render3DEvent(float tick, MatrixStack matrices) {
        this.tick = tick;
        this.matrices = matrices;
    }

    public float getTickDelta() {
        return this.tick;
    }

    public MatrixStack getMatrices() {
        return this.matrices;
    }

    @Override
    public void call(EventListener listener) {
        listener.onRender3D(this);
    }
}

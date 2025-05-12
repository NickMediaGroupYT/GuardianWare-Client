package dev.guardianware.client.events;

import dev.guardianware.api.manager.event.EventArgument;
import dev.guardianware.api.manager.event.EventListener;
import net.minecraft.client.gui.DrawContext;

public class EventRender2D extends EventArgument {
    private final float tick;
    private final DrawContext context;

    public EventRender2D(float tick, DrawContext context) {
        this.tick = tick;
        this.context = context;
    }

    public float getTick() {
        return this.tick;
    }

    public DrawContext getContext() {
        return this.context;
    }

    @Override
    public void call(EventListener listener) {
        listener.onRender2D(this);
    }
}

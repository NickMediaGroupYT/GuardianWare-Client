package dev.guardianware.client.events;

import dev.guardianware.api.manager.event.EventArgument;
import dev.guardianware.api.manager.event.EventListener;
import net.minecraft.entity.LivingEntity;

public class EventDeath extends EventArgument {
    private final LivingEntity entity;
    public EventDeath(LivingEntity entity) {
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    @Override
    public void call(EventListener listener) {
        listener.onDeath(this);
    }
}

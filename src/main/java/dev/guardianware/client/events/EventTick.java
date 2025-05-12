package dev.guardianware.client.events;

import dev.guardianware.api.manager.event.EventArgument;
import dev.guardianware.api.manager.event.EventListener;

public class EventTick extends EventArgument {
    @Override
    public void call(EventListener listener) {
        listener.onTick(this);
    }
}

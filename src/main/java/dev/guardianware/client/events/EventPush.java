package dev.guardianware.client.events;

import dev.guardianware.api.manager.event.EventArgument;
import dev.guardianware.api.manager.event.EventListener;

public class EventPush extends EventArgument {
    @Override
    public void call(EventListener listener) {
        listener.onPush(this);
    }
}

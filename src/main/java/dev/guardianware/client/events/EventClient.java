package dev.guardianware.client.events;

import dev.guardianware.api.manager.event.EventArgument;
import dev.guardianware.api.manager.event.EventListener;
import dev.guardianware.client.values.Value;

public class EventClient extends EventArgument {
    private final Value value;

    public EventClient(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return this.value;
    }

    @Override
    public void call(EventListener listener) {
        listener.onClient(this);
    }
}

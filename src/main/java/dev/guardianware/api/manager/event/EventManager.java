package dev.guardianware.api.manager.event;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private final List<EventListener> LISTENER_REGISTRY;

    public EventManager() {
        super();
        this.LISTENER_REGISTRY = new ArrayList<>();
    }

    public void call(final EventArgument argument) {
        new ArrayList<>(this.LISTENER_REGISTRY).forEach(argument::call);
    }

    public void register(final EventListener listener) {
        this.LISTENER_REGISTRY.add(listener);
    }

    public boolean unregister(final EventListener listener) {
        return this.LISTENER_REGISTRY.remove(listener);
    }
}

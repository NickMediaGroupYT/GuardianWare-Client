package dev.guardianware.api.manager.event;

public abstract class EventArgument {
    private boolean cancel;

    protected EventArgument() {
        super();
        this.cancel = false;
    }

    public final void cancel() {
        this.cancel = true;
    }

    public final boolean isCanceled() {
        return this.cancel;
    }

    public abstract void call(final EventListener listener);
}

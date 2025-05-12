package dev.guardianware.client.values.impl;

import dev.guardianware.client.values.Value;

public class ValueCategory extends Value {
    private boolean open = false;

    public ValueCategory(String name, String description) {
        super(name, name, description);
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}

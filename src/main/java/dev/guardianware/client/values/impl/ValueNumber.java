package dev.guardianware.client.values.impl;

import dev.guardianware.GuardianWare;
import dev.guardianware.client.events.EventClient;
import dev.guardianware.client.values.Value;

public class ValueNumber extends Value {
    public static final int INTEGER = 1;
    public static final int DOUBLE = 2;
    public static final int FLOAT = 3;
    private final Number defaultValue;
    private Number value;
    private final Number minimum;
    private final Number maximum;
    private final ValueCategory parent;

    public ValueNumber(String name, String tag, String description, Number value, Number minimum, Number maximum) {
        super(name, tag, description);
        this.defaultValue = value;
        this.value = value;
        this.minimum = minimum;
        this.maximum = maximum;
        this.parent = null;
    }

    public ValueNumber(String name, String tag, String description, ValueCategory parent, Number value, Number minimum, Number maximum) {
        super(name, tag, description);
        this.defaultValue = value;
        this.value = value;
        this.minimum = minimum;
        this.maximum = maximum;
        this.parent = parent;
    }

    public ValueCategory getParent() {
        return this.parent;
    }

    public Number getDefaultValue() {
        return this.defaultValue;
    }

    public Number getValue() {
        return this.value;
    }

    public void setValue(Number value) {
        this.value = value;
        EventClient event = new EventClient(this);
        GuardianWare.EVENT_MANAGER.call(event);
    }

    public Number getMaximum() {
        return this.maximum;
    }

    public Number getMinimum() {
        return this.minimum;
    }

    public int getType() {
        if (this.value.getClass() == Integer.class) {
            return 1;
        }
        if (this.value.getClass() == Double.class) {
            return 2;
        }
        if (this.value.getClass() == Float.class) {
            return 3;
        }
        return -1;
    }
}

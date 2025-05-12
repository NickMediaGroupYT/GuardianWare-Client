package dev.guardianware.client.values.impl;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.utilities.ColorUtils;
import dev.guardianware.client.events.EventClient;
import dev.guardianware.client.modules.client.ModuleColor;
import dev.guardianware.client.values.Value;

import java.awt.*;

public class ValueColor extends Value {
    private final Color defaultValue;
    private Color value;
    private boolean rainbow;
    private boolean sync;
    private final ValueCategory parent;

    public ValueColor(String name, String tag, String description, Color value) {
        super(name, tag, description);
        this.defaultValue = value;
        this.value = value;
        this.parent = null;
        this.rainbow = false;
        this.sync = false;
    }

    public ValueColor(String name, String tag, String description, ValueCategory parent, Color value) {
        super(name, tag, description);
        this.defaultValue = value;
        this.value = value;
        this.parent = parent;
        this.rainbow = false;
        this.sync = false;
    }

    public ValueCategory getParent() {
        return this.parent;
    }

    public Color getDefaultValue() {
        return this.defaultValue;
    }

    public Color getActualValue() {
        return this.value;
    }

    public Color getValue() {
        if (this.sync && this != ModuleColor.INSTANCE.color) {
            return new Color(ModuleColor.getColor().getRed(), ModuleColor.getColor().getGreen(), ModuleColor.getColor().getBlue(), this.value.getAlpha());
        }
        this.doRainbow();
        return this.value;
    }

    public void setValue(Color value) {
        this.value = value;
        EventClient event = new EventClient(this);
        GuardianWare.EVENT_MANAGER.call(event);
    }

    public boolean isRainbow() {
        return this.rainbow;
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    public boolean isSync() {
        return this.sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    private void doRainbow() {
        if (this.rainbow) {
            Color rainbowColor = ColorUtils.rainbow(1);
            this.setValue(new Color(rainbowColor.getRed(), rainbowColor.getGreen(), rainbowColor.getBlue(), this.value.getAlpha()));
        }
    }
}
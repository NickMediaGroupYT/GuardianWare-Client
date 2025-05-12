package dev.guardianware.client.modules.client;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.client.values.impl.ValueCategory;
import dev.guardianware.client.values.impl.ValueColor;
import dev.guardianware.client.values.impl.ValueNumber;

import java.awt.*;

@RegisterModule(name="Color", description="Manages the client's global color.", category=Module.Category.CLIENT, persistent=true)
public class ModuleColor extends Module {
    public static ModuleColor INSTANCE;
    public final ValueColor color = new ValueColor("Color", "Color", "The client's global color.", new Color(163, 66, 253));
    ValueCategory rainbowCategory = new ValueCategory("Rainbow", "Manage rainbow");
    public ValueNumber rainbowOffset = new ValueNumber("RainbowOffset", "Offset", "", this.rainbowCategory, 255, 0, 1000);
    public ValueNumber rainbowSat = new ValueNumber("RainbowSaturation", "Saturation", "", this.rainbowCategory, 255, 0, 255);
    public ValueNumber rainbowBri = new ValueNumber("RainbowBrightness", "Brightness", "", this.rainbowCategory, 255, 0, 255);

    public ModuleColor() {
        INSTANCE = this;
    }

    public static Color getColor() {
        Color color = INSTANCE == null ? new Color(255, 255, 255) : ModuleColor.INSTANCE.color.getValue();
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
    }

    public static Color getColor(int alpha) {
        Color color = INSTANCE == null ? new Color(255, 255, 255) : ModuleColor.INSTANCE.color.getValue();
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
}
package dev.guardianware.client.modules.client;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.client.values.impl.ValueEnum;
import dev.guardianware.client.values.impl.ValueNumber;

@RegisterModule(name = "Font", category = Module.Category.CLIENT, tag = "Font", description = "Manages client's font.")
public class Font extends Module {

    public final ValueEnum textShadow = new ValueEnum("Text Shadow", "Text Shadow", "Change game text shadow", ShadowModes.DEFAULT);

    public enum ShadowModes {
        NONE,
        DEFAULT,
        CUSTOM
    }

    public final ValueNumber shadowOffset = new ValueNumber("Shadow Offset", "Shadow Offset", "Modifies the game's text shadow offset", 0.5, -2.0, 2.0);

    public float getShadowValue() {
        if (textShadow.getValue().equals(ShadowModes.NONE)) return 0.0f;
        if (textShadow.getValue().equals(ShadowModes.CUSTOM)) return shadowOffset.getValue().floatValue();
        return 1.0f;
    }

}

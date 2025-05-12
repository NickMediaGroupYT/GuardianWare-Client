package dev.guardianware.client.modules.client;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.client.values.impl.ValueNumber;

@RegisterModule(name="Rotations", description="Rotations of the client", category=Module.Category.CLIENT)
public class ModuleRotations extends Module {
    public static ModuleRotations INSTANCE;
    public ValueNumber smoothness = new ValueNumber("RotationSmoothness", "Smoothness", "", 60, 1, 100);

    public ModuleRotations() {
        INSTANCE = this;
    }
}

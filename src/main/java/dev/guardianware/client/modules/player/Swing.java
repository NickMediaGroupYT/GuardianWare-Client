package dev.guardianware.client.modules.player;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.client.values.impl.ValueBoolean;
import dev.guardianware.client.values.impl.ValueNumber;

@RegisterModule(name="Swing", description="", category=Module.Category.PLAYER)
public class Swing extends Module {
    public ValueNumber swingSpeed = new ValueNumber("SwingSpeed", "SwingSpeed", "", 15,1,20);

    public ValueBoolean translateX = new ValueBoolean("TranslateX", "TranslateX", "", false);
    public ValueBoolean translateY = new ValueBoolean("TranslateY", "TranslateY", "", false);
    public ValueBoolean translateZ = new ValueBoolean("TranslateZ", "TranslateZ", "", false);

    public ValueBoolean rotationX = new ValueBoolean("RotateX", "RotateX", "", false);
    public ValueBoolean rotationY = new ValueBoolean("RotateY", "RotateY", "", false);
    public ValueBoolean rotationZ = new ValueBoolean("RotateZ", "RotateZ", "", false);

}

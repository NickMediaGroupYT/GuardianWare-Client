package dev.guardianware.client.modules.visuals;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.client.values.impl.ValueCategory;
import dev.guardianware.client.values.impl.ValueNumber;

@RegisterModule(name="ViewModel", tag="ViewModel", description="", category=Module.Category.VISUALS)
public class ViewModel extends Module {
    public ValueCategory translation = new ValueCategory("Translate", "The category for model translation.");
    public ValueNumber translateX = new ValueNumber("TranslateX", "X", "", translation, 0.0f, -2.0f, 2.0f);
    public ValueNumber translateY = new ValueNumber("TranslateY", "Y", "", translation, 0.0f, -2.0f, 2.0f);
    public ValueNumber translateZ = new ValueNumber("TranslateZ", "Z", "", translation, 0.0f, -2.0f, 2.0f);

    public ValueCategory rotation = new ValueCategory("Rotation", "The category for model translation.");
    public ValueNumber rotateX = new ValueNumber("RotateX", "X", "", rotation, 0, -180, 180);
    public ValueNumber rotateY = new ValueNumber("RotateY", "Y", "", rotation, 0, -180, 180);
    public ValueNumber rotateZ = new ValueNumber("RotateZ", "Z", "", rotation, 0, -180, 180);

    public ValueCategory scale = new ValueCategory("Scale", "The category for model translation.");
    public ValueNumber scaleX = new ValueNumber("ScaleX", "X", "", scale, 1f, 0f, 3f);
    public ValueNumber scaleY = new ValueNumber("ScaleY", "Y", "", scale, 1f, 0f, 3f);
    public ValueNumber scaleZ = new ValueNumber("ScaleZ", "Z", "", scale, 1f, 0f, 3f);
}
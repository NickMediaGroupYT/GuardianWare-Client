package dev.guardianware.client.modules.client;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.client.events.EventKey;
import dev.guardianware.client.values.impl.ValueBoolean;
import dev.guardianware.client.values.impl.ValueNumber;
import org.lwjgl.glfw.GLFW;

@RegisterModule(name="GUI", description="The client's GUI interface for interacting with modules and settings.", category=Module.Category.CLIENT, bind=54)
public class ModuleGUI extends Module {
    public static ModuleGUI INSTANCE;
    public ValueNumber scrollSpeed = new ValueNumber("ScrollSpeed", "Scroll Speed", "The speed for scrolling through the GUI.", 10, 1, 50);
    public ValueBoolean rectEnabled = new ValueBoolean("RectEnabled", "Rect Enabled", "Render a rectangle behind enabled modules.", true);
    public ValueBoolean fadeText = new ValueBoolean("FadeText", "Fade Text", "Add cool animation to the text of the GUI.", false);
    public ValueNumber fadeOffset = new ValueNumber("FadeOffset", "Fade Offset", "Offset for the text animation of the GUI.", 100, 0, 255);

    public ModuleGUI() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null || GuardianWare.CLICK_GUI == null) {
            this.disable(false);
            return;
        }
        mc.setScreen(GuardianWare.CLICK_GUI);
    }

    @Override
    public void onKey(EventKey event) {
        if (event.getKeyCode() == GLFW.GLFW_KEY_ESCAPE && GuardianWare.CLICK_GUI != null) {
            this.disable(false);
        }
    }
}

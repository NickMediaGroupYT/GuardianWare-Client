package dev.guardianware.client.gui.click.components;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.event.EventListener;
import dev.guardianware.client.events.EventKey;
import dev.guardianware.client.gui.click.manage.Component;
import dev.guardianware.client.gui.click.manage.Frame;
import dev.guardianware.client.values.impl.ValueBind;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class BindComponent extends Component implements EventListener {
    private final ValueBind value;
    private boolean binding;

    public BindComponent(ValueBind value, int offset, Frame parent) {
        super(offset, parent);
        GuardianWare.EVENT_MANAGER.register(this);
        this.value = value;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        String keyName;
        int code = this.value.getValue();

        if (binding) {
            keyName = "...";
        } else if (code == 0) {
            keyName = "NONE";
        } else if (code >= 32) {
            keyName = GLFW.glfwGetKeyName(code, 0);
            if (keyName == null) keyName = "KEY_" + code;
        } else {
            // It's a mouse button
            keyName = switch (code) {
                case GLFW.GLFW_MOUSE_BUTTON_LEFT -> "MOUSE1";
                case GLFW.GLFW_MOUSE_BUTTON_RIGHT -> "MOUSE2";
                case GLFW.GLFW_MOUSE_BUTTON_MIDDLE -> "MOUSE3";
                case GLFW.GLFW_MOUSE_BUTTON_4 -> "MOUSE4";
                case GLFW.GLFW_MOUSE_BUTTON_5 -> "MOUSE5";
                default -> "MOUSE" + (code + 1);
            };
        }

        context.drawTextWithShadow(mc.textRenderer,
                this.value.getTag() + " " + Formatting.GRAY + keyName,
                this.getX() + 3,
                this.getY() + 3,
                -1
        );
    }


    @Override
    public void update(int mouseX, int mouseY, float partialTicks) {
        super.update(mouseX, mouseY, partialTicks);
        if (this.value.getParent() != null) {
            this.setVisible(this.value.getParent().isOpen());
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY)) {
            if (!binding) {
                binding = true;
            } else {
                // Set mouse button as bind
                this.value.setValue(mouseButton);
                binding = false;
            }
        }
    }


    @Override
    public void onKey(EventKey event) {
        if (binding) {
            int key = event.getKeyCode();
            if (key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_BACKSPACE) {
                this.value.setValue(0);
            } else if (key != GLFW.GLFW_KEY_ESCAPE) {
                this.value.setValue(key);
            }
            this.binding = false;
        }
    }

}

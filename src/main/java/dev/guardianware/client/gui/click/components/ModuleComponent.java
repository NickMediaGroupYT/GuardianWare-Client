package dev.guardianware.client.gui.click.components;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.utilities.ColorUtils;
import dev.guardianware.api.utilities.RenderUtils;
import dev.guardianware.client.gui.click.manage.Component;
import dev.guardianware.client.gui.click.manage.Frame;
import dev.guardianware.client.modules.client.ModuleColor;
import dev.guardianware.client.modules.client.ModuleGUI;
import dev.guardianware.client.values.Value;
import dev.guardianware.client.values.impl.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModuleComponent extends Component {
    private final ArrayList<Component> components;
    private final Module module;
    private boolean open = false;
    public Map<Integer, Color> colorMap = new HashMap<>();

    public ModuleComponent(Module module, int offset, Frame parent) {
        super(offset, parent);
        this.module = module;
        this.components = new ArrayList<>();
        int valueOffset = offset;
        if (!module.getValues().isEmpty()) {
            for (Value value : module.getValues()) {
                if (value instanceof ValueBoolean) {
                    this.components.add(new BooleanComponent((ValueBoolean)value, valueOffset, parent));
                    valueOffset += 14;
                }
                if (value instanceof ValueNumber) {
                    this.components.add(new NumberComponent((ValueNumber)value, valueOffset, parent));
                    valueOffset += 14;
                }
                if (value instanceof ValueEnum) {
                    this.components.add(new EnumComponent((ValueEnum)value, valueOffset, parent));
                    valueOffset += 14;
                }
                if (value instanceof ValueString) {
                    this.components.add(new StringComponent((ValueString)value, valueOffset, parent));
                    valueOffset += 14;
                }
                if (value instanceof ValueColor) {
                    this.components.add(new ColorComponentTest((ValueColor)value, valueOffset, parent));
                    valueOffset += 14;
                }

                if (value instanceof ValueBind) {
                    this.components.add(new BindComponent((ValueBind)value, valueOffset, parent));
                    valueOffset += 14;
                }
                if (!(value instanceof ValueCategory)) continue;
                this.components.add(new CategoryComponent((ValueCategory)value, valueOffset, parent));
                valueOffset += 14;
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int height = mc.getWindow().getScaledHeight();
        for (int i = 0; i <= height; ++i) {
            this.colorMap.put(i, ColorUtils.wave(Color.WHITE, ModuleGUI.INSTANCE.fadeOffset.getValue().intValue(), i * 2 + 10));
        }
        if (this.module.isToggled() && ModuleGUI.INSTANCE.rectEnabled.getValue()) {
            RenderUtils.drawRect(context.getMatrices(),(float)this.getX() + 0.4f, (float)this.getY() - 0.8f, (float)(this.getX() + this.getWidth()) - 0.4f, (float)this.getY() + 14.1f, GuardianWare.CLICK_GUI.getColor());
        }
        context.drawTextWithShadow(mc.textRenderer, (!this.module.isToggled() ? Formatting.GRAY : "") + this.module.getTag(), this.getX() + 3, this.getY() + 3, ModuleGUI.INSTANCE.fadeText.getValue() ? this.colorMap.get(MathHelper.clamp(this.getY() + 3, 0, height)).getRGB() : -1);
        for (Component component : this.components) {
            component.update(mouseX, mouseY, delta);
        }
        if (this.isOpen()) {
            for (Component component : this.components) {
                Component c;
                if (!component.isVisible()) continue;
                component.render(context, mouseX, mouseY, delta);
                if (component instanceof BooleanComponent) {
                    c = component;
                    if (!component.isHovering(mouseX, mouseY) || ((BooleanComponent)c).getValue().getDescription().isEmpty()) continue;
                    RenderUtils.drawRect(context.getMatrices(),mouseX + 5, mouseY - 2, (float)mouseX + mc.textRenderer.getWidth(((BooleanComponent)c).getValue().getDescription()) + 7.0f, mouseY + 11, new Color(40, 40, 40));
                    RenderUtils.drawOutline(context.getMatrices(),mouseX + 5, mouseY - 2, (float)mouseX + mc.textRenderer.getWidth(((BooleanComponent)c).getValue().getDescription()) + 7.0f, mouseY + 11, 1.0f, ModuleColor.getColor());
                    context.drawTextWithShadow(mc.textRenderer, ((BooleanComponent)c).getValue().getDescription(), mouseX + 7, mouseY, -1);
                    continue;
                }
                if (component instanceof NumberComponent) {
                    c = component;
                    if (!component.isHovering(mouseX, mouseY) || ((NumberComponent)c).getValue().getDescription().isEmpty()) continue;
                    RenderUtils.drawRect(context.getMatrices(),mouseX + 5, mouseY - 2, (float)mouseX + mc.textRenderer.getWidth(((NumberComponent)c).getValue().getDescription()) + 7.0f, mouseY + 11, new Color(40, 40, 40));
                    RenderUtils.drawOutline(context.getMatrices(),mouseX + 5, mouseY - 2, (float)mouseX + mc.textRenderer.getWidth(((NumberComponent)c).getValue().getDescription()) + 7.0f, mouseY + 11, 1.0f, ModuleColor.getColor());
                    context.drawTextWithShadow(mc.textRenderer, ((NumberComponent)c).getValue().getDescription(), mouseX + 7, mouseY, -1);
                    continue;
                }
                if (component instanceof EnumComponent) {
                    c = component;
                    if (!component.isHovering(mouseX, mouseY) || ((EnumComponent)c).getValue().getDescription().isEmpty()) continue;
                    RenderUtils.drawRect(context.getMatrices(),mouseX + 5, mouseY - 2, (float)mouseX + mc.textRenderer.getWidth(((EnumComponent)c).getValue().getDescription()) + 7.0f, mouseY + 11, new Color(40, 40, 40));
                    RenderUtils.drawOutline(context.getMatrices(),mouseX + 5, mouseY - 2, (float)mouseX + mc.textRenderer.getWidth(((EnumComponent)c).getValue().getDescription()) + 7.0f, mouseY + 11, 1.0f, ModuleColor.getColor());
                    context.drawTextWithShadow(mc.textRenderer, ((EnumComponent)c).getValue().getDescription(), mouseX + 7, mouseY, -1);
                    continue;
                }
                if (!(component instanceof StringComponent)) continue;
                c = component;
                if (!component.isHovering(mouseX, mouseY) || ((StringComponent)c).getValue().getDescription().isEmpty()) continue;
                RenderUtils.drawRect(context.getMatrices(), mouseX + 5, mouseY - 2, (float)mouseX + mc.textRenderer.getWidth(((StringComponent)c).getValue().getDescription()) + 7.0f, mouseY + 11, new Color(40, 40, 40));
                RenderUtils.drawOutline(context.getMatrices(), mouseX + 5, mouseY - 2, (float)mouseX + mc.textRenderer.getWidth(((StringComponent)c).getValue().getDescription()) + 7.0f, mouseY + 11, 1.0f, ModuleColor.getColor());
                context.drawTextWithShadow(mc.textRenderer, ((StringComponent)c).getValue().getDescription(), mouseX + 7, mouseY, -1);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            if (mouseButton == 0) {
                this.module.toggle(true);
            }
            if (mouseButton == 1) {
                this.setOpen(!this.open);
                this.getParent().refresh();
            }
        }
        if (this.isOpen()) {
            for (Component component : this.components) {
                if (!component.isVisible()) continue;
                component.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        for (Component component : this.components) {
            component.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    public void charTyped(char typedChar, int keyCode) {
        super.charTyped(typedChar, keyCode);
        if (this.isOpen()) {
            for (Component component : this.components) {
                if (!component.isVisible()) continue;
                component.charTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        if (this.isOpen()) {
            for (Component component : this.components) {
                component.onClose();
            }
        }
    }

    public ArrayList<Component> getComponents() {
        return this.components;
    }

    public Module getModule() {
        return this.module;
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}

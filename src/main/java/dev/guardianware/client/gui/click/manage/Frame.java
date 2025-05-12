package dev.guardianware.client.gui.click.manage;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.element.Element;
import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.utilities.IMinecraft;
import dev.guardianware.api.utilities.RenderUtils;
import dev.guardianware.client.gui.click.components.ColorComponentTest;
import dev.guardianware.client.gui.click.components.ModuleComponent;
import dev.guardianware.client.modules.client.ModuleColor;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.ArrayList;

public class Frame implements IMinecraft {
    private final ArrayList<Component> components;
    private final String tab;
    private int x;
    private int y;
    private int height;
    private final int width;
    private boolean open = true;
    private boolean dragging;
    private int dragX;
    private int dragY;

    public Frame(Module.Category category, int x, int y) {
        this.tab = category.getName();
        this.x = x;
        this.y = y;
        this.width = 100;
        this.dragging = false;
        this.dragX = 0;
        this.dragY = 0;
        this.components = new ArrayList<>();
        int offset = 16;
        for (Module module : GuardianWare.MODULE_MANAGER.getModules(category)) {
            this.components.add(new ModuleComponent(module, offset, this));
            offset += 16;
        }
        this.height = offset;
        this.refresh();
    }

    public Frame(int x, int y) {
        this.tab = "HUD";
        this.x = x;
        this.y = y;
        this.width = 100;
        this.dragging = false;
        this.dragX = 0;
        this.dragY = 0;
        this.components = new ArrayList<>();
        int offset = 16;
        for (Element element : GuardianWare.ELEMENT_MANAGER.getElements()) {
            this.components.add(new ModuleComponent(element, offset, this));
            offset += 16;
        }
        this.height = offset;
        this.refresh();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        this.refresh();
        if (this.isDragging()) {
            this.setX(mouseX - this.dragX);
            this.setY(mouseY - this.dragY);
        }
        RenderUtils.drawRect(context.getMatrices(), this.getX() - 4, this.getY() - 3, this.getX() + this.getWidth() + 4, this.getY() + 13, GuardianWare.CLICK_GUI.getColor());
        if (this.isOpen()) {
            RenderUtils.drawRect(context.getMatrices(), this.getX() - 2, this.getY() + 13, this.getX() + this.getWidth() + 2, this.getY() + this.getHeight(), new Color(0, 0, 0, 160));
            RenderUtils.drawOutline(context.getMatrices(), this.getX() - 2, this.getY() + 13, this.getX() + this.getWidth() + 2, this.getY() + this.getHeight(), 0.5f, ModuleColor.getColor());
        }
        context.drawTextWithShadow(mc.textRenderer, this.tab, this.x + 3, this.y + 1, -1);
        if (this.isOpen()) {
            for (Component component : this.components) {
                if (!component.isVisible()) continue;
                component.render(context, mouseX, mouseY, partialTicks);
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseX >= this.getX() - 4 && mouseX <= this.getX() + this.getWidth() + 4 && mouseY >= this.getY() - 3 && mouseY <= this.getY() + 13) {
            if (mouseButton == 0) {
                this.setDragging(true);
                this.dragX = mouseX - this.getX();
                this.dragY = mouseY - this.getY();
            }
            if (mouseButton == 1) {
                boolean bl = this.open = !this.open;
            }
        }
        if (this.isOpen()) {
            for (Component component : this.components) {
                component.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        this.setDragging(false);
        for (Component component : this.components) {
            component.mouseReleased(mouseX, mouseY, state);
        }
    }

    public void charTyped(char typedChar, int keyCode) {
        if (this.isOpen()) {
            for (Component component : this.components) {
                if (!component.isVisible()) continue;
                component.charTyped(typedChar, keyCode);
            }
        }
    }

    public void refresh() {
        int offset = 16;
        for (Component component : this.components) {
            ModuleComponent moduleComponent;
            if (!component.isVisible()) continue;
            component.setOffset(offset);
            offset += 16;
            if (!(component instanceof ModuleComponent) || (moduleComponent = (ModuleComponent)component).getModule().getValues().isEmpty() || !moduleComponent.isOpen()) continue;
            for (Component valueComponent : moduleComponent.getComponents()) {
                if (!valueComponent.isVisible()) continue;
                valueComponent.setOffset(offset);
                offset += valueComponent instanceof ColorComponentTest && ((ColorComponentTest)valueComponent).isOpen() ? 190 : 14;
            }
        }
        this.setHeight(offset);
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public boolean isOpen() {
        return this.open;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public ArrayList<Component> getComponents() {
        return this.components;
    }
}

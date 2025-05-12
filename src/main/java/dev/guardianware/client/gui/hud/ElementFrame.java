package dev.guardianware.client.gui.hud;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.element.Element;
import dev.guardianware.api.utilities.IMinecraft;
import dev.guardianware.api.utilities.RenderUtils;
import dev.guardianware.client.events.EventRender2D;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class ElementFrame implements IMinecraft {
    private final Element element;
    private float x;
    private float y;
    private float width;
    private float height;
    private float dragX;
    private float dragY;
    private boolean dragging;
    private boolean visible;
    private HudEditorScreen parent;

    public ElementFrame(Element element, float x, float y, float width, float height, HudEditorScreen parent) {
        this.element = element;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.parent = parent;
        this.dragging = false;
        this.visible = true;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        if (this.element != null && GuardianWare.ELEMENT_MANAGER.isElementEnabled(this.element.getName()) && mc.getWindow() != null) {
            if (this.dragging) {
                this.x = this.dragX + (float)mouseX;
                this.y = this.dragY + (float)mouseY;
                if ((double)this.x < 0.0) {
                    this.x = 0.0f;
                }
                if ((double)this.y < 0.0) {
                    this.y = 0.0f;
                }
                if (this.x > (float)mc.getWindow().getScaledWidth() - this.width) {
                    this.x = (float)mc.getWindow().getScaledWidth() - this.width;
                }
                if (this.y > (float)mc.getWindow().getScaledHeight() - this.height) {
                    this.y = (float)mc.getWindow().getScaledHeight() - this.height;
                }
            }
            if (this.dragging) {
                RenderUtils.drawRect(context.getMatrices(), this.x, this.y, this.x + this.width, this.y + this.height, new Color(Color.DARK_GRAY.getRed(), Color.DARK_GRAY.getGreen(), Color.DARK_GRAY.getBlue(), 100));
            } else {
                RenderUtils.drawRect(context.getMatrices(), this.x, this.y, this.x + this.width, this.y + this.height, new Color(Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue(), 100));
            }
            this.element.onRender2D(new EventRender2D(partialTicks, context));
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.dragX = this.x - (float)mouseX;
            this.dragY = this.y - (float)mouseY;
            this.dragging = true;
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        this.dragging = false;
    }

    public boolean isHovering(int mouseX, int mouseY) {
        return (float)mouseX >= this.x && (float)mouseX <= this.x + this.width && (float)mouseY >= this.y && (float)mouseY <= this.y + this.height;
    }

    public Element getElement() {
        return this.element;
    }

    public HudEditorScreen getParent() {
        return this.parent;
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return this.width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}

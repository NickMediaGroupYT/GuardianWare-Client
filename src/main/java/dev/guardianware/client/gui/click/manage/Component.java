package dev.guardianware.client.gui.click.manage;

import dev.guardianware.api.utilities.IMinecraft;
import net.minecraft.client.gui.DrawContext;

public class Component implements IMinecraft {
    private boolean visible;
    private int offset;
    private final int width;
    private final int height;
    private final Frame parent;

    public Component(int offset, Frame parent) {
        this.offset = offset;
        this.parent = parent;
        this.width = parent.getWidth();
        this.height = 14;
        this.visible = true;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
    }

    public void charTyped(char c, int keyCode) {
    }

    public void onClose() {
    }

    public void update(int mouseX, int mouseY, float partialTicks) {
    }

    public boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.getHeight();
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getOffset() {
        return this.offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getX() {
        return this.parent.getX();
    }

    public int getY() {
        return this.parent.getY() + this.offset;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Frame getParent() {
        return this.parent;
    }
}

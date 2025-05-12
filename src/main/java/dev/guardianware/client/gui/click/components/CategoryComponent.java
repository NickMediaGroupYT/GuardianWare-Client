package dev.guardianware.client.gui.click.components;

import dev.guardianware.client.gui.click.manage.Component;
import dev.guardianware.client.gui.click.manage.Frame;
import dev.guardianware.client.values.impl.ValueCategory;
import net.minecraft.client.gui.DrawContext;

public class CategoryComponent extends Component {
    private final ValueCategory value;

    public CategoryComponent(ValueCategory value, int offset, Frame parent) {
        super(offset, parent);
        this.value = value;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        context.drawTextWithShadow(mc.textRenderer, this.value.getName(), this.getX() + 3, this.getY() + 3, -1);
        context.drawTextWithShadow(mc.textRenderer, this.value.isOpen() ? "-" : "+", (int) ((float)(this.getX() + this.getWidth() - 3) - mc.textRenderer.getWidth(this.value.isOpen() ? "+" : "-")), this.getY() + 3, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.isHovering(mouseX, mouseY) && mouseButton == 1) {
            this.value.setOpen(!this.value.isOpen());
            this.getParent().refresh();
        }
    }
}
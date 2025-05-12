package dev.guardianware.client.gui.click.components;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.utilities.RenderUtils;
import dev.guardianware.client.gui.click.manage.Component;
import dev.guardianware.client.gui.click.manage.Frame;
import dev.guardianware.client.values.impl.ValueEnum;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;

public class EnumComponent extends Component {
    private final ValueEnum value;
    private int enumSize;

    public EnumComponent(ValueEnum value, int offset, Frame parent) {
        super(offset, parent);
        this.value = value;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        RenderUtils.drawRect(context.getMatrices(), this.getX() + 1, this.getY(), this.getX() + this.getWidth() - 1, this.getY() + 14, GuardianWare.CLICK_GUI.getColor());
        context.drawTextWithShadow(mc.textRenderer, this.value.getTag() + " " + Formatting.GRAY + this.value.getValue().toString(), this.getX() + 3, this.getY() + 3, -1);
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
        if (mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.getHeight()) {
            if (mouseButton == 0) {
                int maxIndex = this.value.getEnums().size() - 1;
                ++this.enumSize;
                if (this.enumSize > maxIndex) {
                    this.enumSize = 0;
                }
                this.value.setValue(this.value.getEnums().get(this.enumSize));
            } else if (mouseButton == 1) {
                int maxIndex = this.value.getEnums().size() - 1;
                --this.enumSize;
                if (this.enumSize < 0) {
                    this.enumSize = maxIndex;
                }
                this.value.setValue(this.value.getEnums().get(this.enumSize));
            }
        }
    }

    public ValueEnum getValue() {
        return this.value;
    }
}

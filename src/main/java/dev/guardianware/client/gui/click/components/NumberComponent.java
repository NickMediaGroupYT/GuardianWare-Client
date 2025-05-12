package dev.guardianware.client.gui.click.components;

import com.sun.jna.platform.KeyboardUtils;
import dev.guardianware.GuardianWare;
import dev.guardianware.api.utilities.MathUtils;
import dev.guardianware.api.utilities.RenderUtils;
import dev.guardianware.api.utilities.TimerUtils;
import dev.guardianware.client.gui.click.manage.Component;
import dev.guardianware.client.gui.click.manage.Frame;
import dev.guardianware.client.values.impl.ValueNumber;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;

public class NumberComponent extends Component {
    private final ValueNumber value;
    private float sliderWidth;
    private boolean dragging;
    private boolean listening;
    private String currentString = "";
    private final TimerUtils timer = new TimerUtils();
    private final TimerUtils backTimer = new TimerUtils();
    private boolean selecting = false;
    private boolean line = false;
    private boolean binding = false;
    private int boundMouseButton = -1;

    public NumberComponent(ValueNumber value, int offset, Frame parent) {
        super(offset, parent);
        this.value = value;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        if (this.timer.hasTimeElapsed(400L)) {
            this.line = !this.line;
            this.timer.reset();
        }
        RenderUtils.drawRect(context.getMatrices(),this.getX() + 1, this.getY(), (float)(this.getX() + 1) + this.sliderWidth, this.getY() + 14, GuardianWare.CLICK_GUI.getColor());
        if (this.selecting) {
            RenderUtils.drawRect(context.getMatrices(),(float)(this.getX() + 3) + mc.textRenderer.getWidth(this.value.getTag() + " "), this.getY() + 3, (float)(this.getX() + 3) + mc.textRenderer.getWidth(this.value.getTag() + " ") + mc.textRenderer.getWidth(this.currentString), (float)this.getY() + mc.textRenderer.fontHeight + 3.0f, new Color(Color.LIGHT_GRAY.getRed(), Color.LIGHT_GRAY.getGreen(), Color.LIGHT_GRAY.getBlue(), 100));
        }
        if (this.listening) {
            context.drawTextWithShadow(mc.textRenderer, this.value.getTag() + " " + Formatting.GRAY + this.currentString + (this.selecting ? "" : (this.line ? "|" : "")), this.getX() + 3, this.getY() + 3, -1);
        } else {
            context.drawTextWithShadow(mc.textRenderer, this.value.getTag() + " " + Formatting.GRAY + this.value.getValue() + (this.value.getType() == 1 ? ".0" : ""), this.getX() + 3, this.getY() + 3, -1);
        }
    }

    @Override
    public void update(int mouseX, int mouseY, float partialTicks) {
        super.update(mouseX, mouseY, partialTicks);
        if (this.value.getParent() != null) {
            this.setVisible(this.value.getParent().isOpen());
        }
        double difference = Math.min(98, Math.max(0, mouseX - this.getX()));
        if (this.value.getType() == 1) {
            this.sliderWidth = 98.0f * (float)(this.value.getValue().intValue() - this.value.getMinimum().intValue()) / (float)(this.value.getMaximum().intValue() - this.value.getMinimum().intValue());
            if (this.dragging) {
                if (difference == 0.0) {
                    this.value.setValue(this.value.getMinimum());
                } else {
                    int value = (int) MathUtils.roundToPlaces(difference / 98.0 * (double)(this.value.getMaximum().intValue() - this.value.getMinimum().intValue()) + (double)this.value.getMinimum().intValue(), 0);
                    this.value.setValue(value);
                }
            }
        } else if (this.value.getType() == 2) {
            this.sliderWidth = (float)(98.0 * (this.value.getValue().doubleValue() - this.value.getMinimum().doubleValue()) / (this.value.getMaximum().doubleValue() - this.value.getMinimum().doubleValue()));
            if (this.dragging) {
                if (difference == 0.0) {
                    this.value.setValue(this.value.getMinimum());
                } else {
                    double value = MathUtils.roundToPlaces(difference / 98.0 * (this.value.getMaximum().doubleValue() - this.value.getMinimum().doubleValue()) + this.value.getMinimum().doubleValue(), 2);
                    this.value.setValue(value);
                }
            }
        } else if (this.value.getType() == 3) {
            this.sliderWidth = 98.0f * (this.value.getValue().floatValue() - this.value.getMinimum().floatValue()) / (this.value.getMaximum().floatValue() - this.value.getMinimum().floatValue());
            if (this.dragging) {
                if (difference == 0.0) {
                    this.value.setValue(this.value.getMinimum());
                } else {
                    float value = (float)MathUtils.roundToPlaces(difference / 98.0 * (double)(this.value.getMaximum().floatValue() - this.value.getMinimum().floatValue()) + (double)this.value.getMinimum().floatValue(), 2);
                    this.value.setValue(value);
                }
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.isHovering(mouseX, mouseY)) {
            if (mouseButton == 0) {
                this.dragging = true;
            } else if (mouseButton == 1) {
                this.listening = !this.listening;
                this.currentString = this.value.getValue().toString();
            } else {
                // Handle other buttons as binds
                if (this.binding) {
                    this.boundMouseButton = mouseButton;
                    this.binding = false;
                    System.out.println("Bound to mouse button: " + mouseButton); // optional log
                }
            }
        }
    }


    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.dragging = false;
    }

    @Override
    public void charTyped(char typedChar, int keyCode) {
        super.charTyped(typedChar, keyCode);
        this.backTimer.reset();
        if (this.listening) {
            if (keyCode == 1) {
                this.selecting = false;
                return;
            }
            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                this.updateString();
                this.selecting = false;
                this.listening = false;
            } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                this.currentString = this.selecting ? "" : this.removeLastCharacter(this.currentString);
                this.selecting = false;
            } else if (keyCode == 47 && (KeyboardUtils.isPressed(157) || KeyboardUtils.isPressed(29))) {
                try {
                    this.currentString = this.currentString + Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            } else if (isValidChatCharacter(typedChar)) {
                this.currentString = this.selecting ? "" + typedChar : this.currentString + typedChar;
                this.selecting = false;
            }
            if (keyCode == 30 && KeyboardUtils.isPressed(29)) {
                this.selecting = true;
            }
        }
    }

    private void updateString() {
        block1:
        if (this.currentString.length() > 0) {
            if (this.value.getType() == 1) {
                try {
                    if (Integer.parseInt(this.currentString) <= this.value.getMaximum().intValue() && Integer.parseInt(this.currentString) >= this.value.getMinimum().intValue()) {
                        this.value.setValue(Integer.parseInt(this.currentString));
                        break block1;
                    }
                    this.value.setValue(this.value.getValue());
                } catch (NumberFormatException e) {
                    this.value.setValue(this.value.getValue());
                }
            } else if (this.value.getType() == 3) {
                try {
                    if (!(Float.parseFloat(this.currentString) > this.value.getMaximum().floatValue()) && !(Float.parseFloat(this.currentString) < this.value.getMinimum().floatValue())) {
                        this.value.setValue(Float.parseFloat(this.currentString));
                        break block1;
                    }
                    this.value.setValue(this.value.getValue());
                } catch (NumberFormatException e) {
                    this.value.setValue(this.value.getValue());
                }
            } else if (this.value.getType() == 2) {
                try {
                    if (!(Double.parseDouble(this.currentString) > this.value.getMaximum().doubleValue()) && !(Double.parseDouble(this.currentString) < this.value.getMinimum().doubleValue())) {
                        this.value.setValue(Double.parseDouble(this.currentString));
                    } else {
                        this.value.setValue(this.value.getValue());
                    }
                } catch (NumberFormatException e) {
                    this.value.setValue(this.value.getValue());
                }
            }
        }
        this.currentString = "";
    }

    private boolean isValidChatCharacter(char c) {
        return c >= ' ' && c != 127;
    }

    private String removeLastCharacter(String input) {
        if (input.length() > 0) {
            return input.substring(0, input.length() - 1);
        }
        return input;
    }

    public ValueNumber getValue() {
        return this.value;
    }
}
package dev.guardianware.client.modules.visuals;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.RenderUtils;
import dev.guardianware.client.events.EventRender2D;
import dev.guardianware.client.values.impl.ValueBoolean;
import dev.guardianware.client.values.impl.ValueCategory;
import dev.guardianware.client.values.impl.ValueColor;
import dev.guardianware.client.values.impl.ValueNumber;

import java.awt.*;

@RegisterModule(name="Crosshair", tag="Crosshair", description="Change how the default minecraft crosshair looks.", category=Module.Category.VISUALS)
public class ModuleCrosshair extends Module {
    private final ValueCategory crosshairCategory = new ValueCategory("Crosshair", "The category for the configuration of the crosshair.");
    private final ValueBoolean dynamic = new ValueBoolean("Dynamic", "Dynamic", "Makes it so that the crosshair expands when holding down keys.", this.crosshairCategory, true);
    private final ValueBoolean attackIndicator = new ValueBoolean("AttackIndicator", "Attack Indicator", "Renders an attack indicator below the crosshair to show you your combat delay.", this.crosshairCategory, true);
    private final ValueNumber length = new ValueNumber("Length", "Length", "The length of the parts of the crosshair.", this.crosshairCategory, 10.0f, 0.0f, 25.0f);
    private final ValueNumber partWidth = new ValueNumber("Width", "Width", "The width of the parts of the crosshair.", this.crosshairCategory, 2.5f, 0.0f, 25.0f);
    private final ValueNumber gap = new ValueNumber("Gap", "Gap", "The gap space on every part of the crosshair.", this.crosshairCategory, 6.1f, 0.0f, 25.0f);
    private final ValueCategory fillCategory = new ValueCategory("Fill", "The category for the filling of the crosshair parts.");
    private final ValueBoolean fill = new ValueBoolean("Fill", "Fill", "Fills every crosshair part.", this.fillCategory, true);
    private final ValueColor fillColor = new ValueColor("FillColor", "Color", "The color for the filling.", this.fillCategory, new Color(0, 0, 255));
    private final ValueCategory outlineCategory = new ValueCategory("Outline", "The category for outlining the crosshair parts.");
    private final ValueBoolean outline = new ValueBoolean("Outline", "Outline", "Outlines every crosshair part.", this.outlineCategory, true);
    private final ValueNumber outlineWidth = new ValueNumber("OutlineWidth", "Width", "The width for the outlining.", this.outlineCategory, 1.0f, 0.0f, 5.0f);
    private final ValueColor outlineColor = new ValueColor("OutlineColor", "Color", "The color for the outlining.", this.outlineCategory, new Color(0, 0, 0));

    @Override
    public void onRender2D(EventRender2D event) {
        float width = (float)mc.getWindow().getScaledWidth() / 2.0f;
        float height = (float)mc.getWindow().getScaledHeight() / 2.0f;
        if (this.fill.getValue()) {
            RenderUtils.drawRect(event.getContext().getMatrices(),width - this.gap.getValue().floatValue() - this.length.getValue().floatValue() - (this.isMoving() ? 2.0f : 0.0f), height - this.partWidth.getValue().floatValue() / 2.0f, width - this.gap.getValue().floatValue() - this.length.getValue().floatValue() - (this.isMoving() ? 2.0f : 0.0f) + this.length.getValue().floatValue(), height - this.partWidth.getValue().floatValue() / 2.0f + this.partWidth.getValue().floatValue(), this.fillColor.getValue());
            RenderUtils.drawRect(event.getContext().getMatrices(),width + this.gap.getValue().floatValue() + (float)(this.isMoving() ? 2 : 0), height - this.partWidth.getValue().floatValue() / 2.0f, width + this.gap.getValue().floatValue() + (float)(this.isMoving() ? 2 : 0) + this.length.getValue().floatValue(), height - this.partWidth.getValue().floatValue() / 2.0f + this.partWidth.getValue().floatValue(), this.fillColor.getValue());
            RenderUtils.drawRect(event.getContext().getMatrices(),width - this.partWidth.getValue().floatValue() / 2.0f, height - this.gap.getValue().floatValue() - this.length.getValue().floatValue() - (float)(this.isMoving() ? 2 : 0), width - this.partWidth.getValue().floatValue() / 2.0f + this.partWidth.getValue().floatValue(), height - this.gap.getValue().floatValue() - this.length.getValue().floatValue() - (float)(this.isMoving() ? 2 : 0) + this.length.getValue().floatValue(), this.fillColor.getValue());
            RenderUtils.drawRect(event.getContext().getMatrices(),width - this.partWidth.getValue().floatValue() / 2.0f, height + this.gap.getValue().floatValue() + (float)(this.isMoving() ? 2 : 0), width - this.partWidth.getValue().floatValue() / 2.0f + this.partWidth.getValue().floatValue(), height + this.gap.getValue().floatValue() + (float)(this.isMoving() ? 2 : 0) + this.length.getValue().floatValue(), this.fillColor.getValue());
            if (this.attackIndicator.getValue() && mc.player.getAttackCooldownProgress(0.0f) < 1.0f) {
                RenderUtils.drawRect(event.getContext().getMatrices(),width - 10.0f, height + this.gap.getValue().floatValue() + this.length.getValue().floatValue() + (float)(this.isMoving() ? 2 : 0) + 2.0f, width - 10.0f + mc.player.getAttackCooldownProgress(0.0f) * 20.0f, height + this.gap.getValue().floatValue() + this.length.getValue().floatValue() + (float)(this.isMoving() ? 2 : 0) + 2.0f + 2.0f, this.fillColor.getValue());
            }
        }
        if (this.outline.getValue()) {
            RenderUtils.drawOutline(event.getContext().getMatrices(),width - this.gap.getValue().floatValue() - this.length.getValue().floatValue() - (this.isMoving() ? 2.0f : 0.0f), height - this.partWidth.getValue().floatValue() / 2.0f, width - this.gap.getValue().floatValue() - this.length.getValue().floatValue() - (this.isMoving() ? 2.0f : 0.0f) + this.length.getValue().floatValue(), height - this.partWidth.getValue().floatValue() / 2.0f + this.partWidth.getValue().floatValue(), this.outlineWidth.getValue().floatValue(), this.outlineColor.getValue());
            RenderUtils.drawOutline(event.getContext().getMatrices(),width + this.gap.getValue().floatValue() + (float)(this.isMoving() ? 2 : 0), height - this.partWidth.getValue().floatValue() / 2.0f, width + this.gap.getValue().floatValue() + (float)(this.isMoving() ? 2 : 0) + this.length.getValue().floatValue(), height - this.partWidth.getValue().floatValue() / 2.0f + this.partWidth.getValue().floatValue(), this.outlineWidth.getValue().floatValue(), this.outlineColor.getValue());
            RenderUtils.drawOutline(event.getContext().getMatrices(),width - this.partWidth.getValue().floatValue() / 2.0f, height - this.gap.getValue().floatValue() - this.length.getValue().floatValue() - (float)(this.isMoving() ? 2 : 0), width - this.partWidth.getValue().floatValue() / 2.0f + this.partWidth.getValue().floatValue(), height - this.gap.getValue().floatValue() - this.length.getValue().floatValue() - (float)(this.isMoving() ? 2 : 0) + this.length.getValue().floatValue(), this.outlineWidth.getValue().floatValue(), this.outlineColor.getValue());
            RenderUtils.drawOutline(event.getContext().getMatrices(),width - this.partWidth.getValue().floatValue() / 2.0f, height + this.gap.getValue().floatValue() + (float)(this.isMoving() ? 2 : 0), width - this.partWidth.getValue().floatValue() / 2.0f + this.partWidth.getValue().floatValue(), height + this.gap.getValue().floatValue() + (float)(this.isMoving() ? 2 : 0) + this.length.getValue().floatValue(), this.outlineWidth.getValue().floatValue(), this.outlineColor.getValue());
            if (this.attackIndicator.getValue() && mc.player.getAttackCooldownProgress(0.0f) < 1.0f) {
                RenderUtils.drawOutline(event.getContext().getMatrices(),width - 10.0f, height + this.gap.getValue().floatValue() + this.length.getValue().floatValue() + (float)(this.isMoving() ? 2 : 0) + 2.0f, width - 10.0f + mc.player.getAttackCooldownProgress(0.0f) * 20.0f, height + this.gap.getValue().floatValue() + this.length.getValue().floatValue() + (float)(this.isMoving() ? 2 : 0) + 2.0f + 2.0f, this.outlineWidth.getValue().floatValue(), this.outlineColor.getValue());
            }
        }
    }

    boolean isMoving() {
        return (mc.player.isSneaking() || mc.player.sidewaysSpeed != 0.0f || mc.player.forwardSpeed != 0.0f || !mc.player.isOnGround()) && this.dynamic.getValue();
    }
}
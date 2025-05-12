package dev.guardianware.client.modules.visuals;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.RenderUtils;
import dev.guardianware.client.events.Render3DEvent;
import dev.guardianware.client.values.impl.ValueBoolean;
import dev.guardianware.client.values.impl.ValueColor;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

import java.awt.*;

@RegisterModule(name = "Chams", tag = "Chams", description = "Renders players through walls", category = Module.Category.VISUALS)
public class Chams extends Module {

    private final ValueBoolean renderThroughWalls = new ValueBoolean("ThroughWalls", "ThroughWalls", "Render players through walls", true);
    private final ValueColor visibleColor = new ValueColor("VisibleColor", "VisibleColor", "", new Color(0, 255, 0, 80));
    private final ValueColor hiddenColor = new ValueColor("HiddenColor", "HiddenColor", "", new Color(255, 0, 0, 80));

    @Override
    public void onRender3D(Render3DEvent event) {
        if (mc.world == null || mc.player == null) return;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || !(player instanceof AbstractClientPlayerEntity)) continue;

            Box box = player.getBoundingBox().expand(0.1);

            // Render behind walls
            if (renderThroughWalls.getValue()) {
                RenderUtils.setupRender();
                RenderUtils.drawBoxFilled(event.getMatrices(), box, hiddenColor.getValue());
                RenderUtils.endRender();
            }

            // Render on top (visible part)
            RenderUtils.setupRender();
            RenderUtils.drawBoxFilled(event.getMatrices(), box, visibleColor.getValue());
            RenderUtils.endRender();
        }
    }
}

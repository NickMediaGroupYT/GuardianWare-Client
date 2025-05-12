package dev.guardianware.client.modules.visuals;


import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.DirectionUtils;
import dev.guardianware.api.utilities.DirectionUtils.EightWayDirections;
import dev.guardianware.api.utilities.RenderUtils;
import dev.guardianware.client.events.Render3DEvent;
import dev.guardianware.client.values.impl.ValueNumber;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

import java.awt.*;

@RegisterModule(name="PhaseESP", tag="PhaseESP", description="dont render some things.", category=Module.Category.VISUALS)
public class PhaseESP extends Module {
    public final ValueNumber boxAlpha = new ValueNumber("BoxAlpha", "BoxAlpha", "", 15,0,255);
    public final ValueNumber lineAlpha = new ValueNumber("LineAlpha", "LineAlpha", "", 50,0,255);
    public final ValueNumber lineWidth = new ValueNumber("LineWidth", "LineWidth", "", 2.0,0.1,4.0);
    public final ValueNumber fadeDistance = new ValueNumber("FadeDistance", "FadeDistance", "", 0.5,0.0,1.0);

    @Override
    public void onRender3D(Render3DEvent event) {
        if (mc.player != null && mc.world != null && mc.player.isOnGround()) {
            BlockPos playerPos = mc.player.getBlockPos();

            for (DirectionUtils.EightWayDirections direction : DirectionUtils.EightWayDirections.values()) {
                BlockPos blockPos = direction.offset(playerPos);
                phaseESPRender(blockPos, event, direction);
            }
        }
    }

    private void phaseESPRender(BlockPos blockPos, Render3DEvent event, EightWayDirections direction) {
        if (!mc.world.getBlockState(blockPos).isReplaceable()) {
            BlockState state = mc.world.getBlockState(blockPos.down());
            Color color;

            // Determine block safety
            if (state.isReplaceable()) {
                color = new Color(255, 0, 0, boxAlpha.getValue().intValue()); // Red: Unsafe
            } else if (state.getHardness(mc.world, blockPos.down()) < 0) {
                color = new Color(0, 255, 0, boxAlpha.getValue().intValue()); // Green: Safe
            } else {
                color = new Color(255, 255, 0, boxAlpha.getValue().intValue()); // Yellow: Neutral
            }

            // Determine player's position relative to the block
            BlockPos playerPos = mc.player.getBlockPos();
            Vec3d pos = mc.player.getPos();
            double dx = pos.getX() - (double) playerPos.getX();
            double dz = pos.getZ() - (double) playerPos.getZ();

            double far = fadeDistance.getValue().doubleValue();
            double near = 1.0 - fadeDistance.getValue().doubleValue();

            // Render based on direction
            if (direction == EightWayDirections.EAST && dx >= far) {
                BoxRender(blockPos, event, color);
            } else if (direction == EightWayDirections.WEST && dx <= near) {
                BoxRender(blockPos, event, color);
            } else if (direction == DirectionUtils.EightWayDirections.SOUTH && dz >= far) {
                BoxRender(blockPos, event, color);
            } else if (direction == DirectionUtils.EightWayDirections.NORTH && dz <= near) {
                BoxRender(blockPos, event, color);
            } else if (direction == DirectionUtils.EightWayDirections.NORTHEAST && dz <= near && dx >= far) {
                BoxRender(blockPos, event, color);
            } else if (direction == DirectionUtils.EightWayDirections.NORTHWEST && dz <= near && dx <= near) {
                BoxRender(blockPos, event, color);
            } else if (direction == DirectionUtils.EightWayDirections.SOUTHEAST && dz >= far && dx >= far) {
                BoxRender(blockPos, event, color);
            } else if (direction == DirectionUtils.EightWayDirections.SOUTHWEST && dz >= far && dx <= near) {
                BoxRender(blockPos, event, color);
            }
        }
    }

    private void BoxRender(BlockPos blockPos, Render3DEvent event, Color color) {
        RenderUtils.setupRender();
        Box render1 = VoxelShapes.fullCube().getBoundingBox();
        Box render = new Box(
                (double) blockPos.getX() + render1.minX,
                (double) blockPos.getY() + render1.minY,
                (double) blockPos.getZ() + render1.minZ,
                (double) blockPos.getX() + render1.maxX,
                (double) blockPos.getY() + render1.minY,
                (double) blockPos.getZ() + render1.maxZ
        );
        RenderUtils.drawBoxFilled(event.getMatrices(), render, color);
        RenderUtils.drawBox(event.getMatrices(), render, color, lineWidth.getValue().floatValue());
        RenderUtils.endRender();
    }


}

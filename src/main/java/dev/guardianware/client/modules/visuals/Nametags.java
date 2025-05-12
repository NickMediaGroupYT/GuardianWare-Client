package dev.guardianware.client.modules.visuals;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.RenderUtils;
import dev.guardianware.client.events.Render3DEvent;
import dev.guardianware.client.values.impl.ValueBoolean;
import dev.guardianware.client.values.impl.ValueColor;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

import static dev.guardianware.client.modules.combat.AimBot.getDistanceTo;

@RegisterModule(name="Nametags", tag="Nametags", description="Better player nametags.", category=Module.Category.VISUALS)
public class Nametags extends Module {
    ValueBoolean players = new ValueBoolean("Players", "Players", "", true);
    ValueBoolean pearls = new ValueBoolean("Pearls", "Pearls", "", true);
    ValueColor textColor = new ValueColor("TextColor", "TextColor", "", Color.WHITE);
    ValueBoolean textShadow = new ValueBoolean("TextShadow", "TextShadow", "", true);
    ValueColor fillColor = new ValueColor("FillColor", "FillColor", "", new Color(122, 122, 122, 94));
    ValueColor lineColor = new ValueColor("OutlineColor", "OutlineColor", "", new Color(255, 255, 255, 255));

    @Override
    public void onRender3D(Render3DEvent event) {
        if (players.getValue()) {
            for (PlayerEntity player : mc.world.getPlayers()) {
                if (player == mc.player) continue;

                float interpolatedTick = event.getTickDelta();

                Vec3d pos;
                double lerpX = player.prevX + (player.getX() - player.prevX) * interpolatedTick;
                double lerpY = player.prevY + (player.getY() - player.prevY) * interpolatedTick;
                double lerpZ = player.prevZ + (player.getZ() - player.prevZ) * interpolatedTick;
                pos = new Vec3d(lerpX, lerpY, lerpZ);

                pos = pos.add(0, player.getEyeHeight(player.getPose()) + 0.625, 0);

                float distance = (float) mc.getEntityRenderDispatcher().camera.getPos().squaredDistanceTo(pos.x, pos.y, pos.z);
                distance = (float) Math.sqrt(distance);
                float scaling = 0.0018f + (30 / 10000.0f) * distance;
                if (distance <= 8.0) scaling = 0.0245f;

                MatrixStack matrices = new MatrixStack();
                RenderSystem.setShader(GameRenderer::getPositionColorProgram);
                RenderSystem.applyModelViewMatrix();
                RenderUtils.setupRender();

                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(mc.gameRenderer.getCamera().getPitch()));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(mc.gameRenderer.getCamera().getYaw() + 180f));
                Camera camera = mc.gameRenderer.getCamera();

                String name = player.getName().getLiteralString();
                String health = String.valueOf(Math.round(player.getHealth()));
                int totalWidth = mc.textRenderer.getWidth(name + " " + health);
                Color color = GuardianWare.FRIEND_MANAGER.isFriend(player.getName().getLiteralString())
                        ? new Color(61, 255, 212, 255)
                        : textColor.getValue();

                RenderUtils.drawTextIn3D(matrices, (-totalWidth / 2) + (mc.textRenderer.getWidth(name) / 2), 0, name, pos, color, textShadow.getValue());
                RenderUtils.drawTextIn3D(matrices, (totalWidth / 2) - (mc.textRenderer.getWidth(health) / 2), 0, health, pos, Color.GREEN, textShadow.getValue());

                matrices.translate(pos.x - camera.getPos().x, pos.y - camera.getPos().y, pos.z - camera.getPos().z);
                matrices.multiply(mc.getEntityRenderDispatcher().getRotation());
                matrices.scale(scaling, -scaling, scaling);

                RenderUtils.renderQuad(matrices, -totalWidth / 2 - 2, 9, totalWidth / 2 + 2, -2, fillColor.getValue());
                RenderUtils.renderOutline(matrices, -totalWidth / 2 - 2, 9, totalWidth / 2 + 2, -2, lineColor.getValue());
                RenderUtils.endRender();
            }
        }

        if (pearls.getValue()) {
            for (Entity pearl : mc.world.getEntities()) {
                if (pearl instanceof EnderPearlEntity epearl) {
                    Vec3d pos;
                    if (getDistanceTo(pearl) < 2) continue;
                    if (epearl.getOwner() != null) {
                        RenderUtils.setupRender();
                        float partialTicks = event.getTickDelta();
                        double lerpX = epearl.prevX + (epearl.getX() - epearl.prevX) * partialTicks;
                        double lerpY = epearl.prevY + (epearl.getY() - epearl.prevY) * partialTicks;
                        double lerpZ = epearl.prevZ + (epearl.getZ() - epearl.prevZ) * partialTicks;
                        pos = new Vec3d(lerpX, lerpY, lerpZ);
                        RenderUtils.drawTextIn3D(event.getMatrices(), 0, 0, epearl.getOwner().getName().getLiteralString(), pos, textColor.getValue(), textShadow.getValue());


                        float distance = (float) mc.getEntityRenderDispatcher().camera.getPos().squaredDistanceTo(pos.x, pos.y, pos.z);
                        distance = (float) Math.sqrt(distance);
                        float scaling = 0.0018f + (30 / 10000.0f) * distance;
                        if (distance <= 8.0) scaling = 0.0245f;
                        MatrixStack matrices = new MatrixStack();
                        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
                        RenderSystem.applyModelViewMatrix();
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(mc.gameRenderer.getCamera().getPitch()));
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(mc.gameRenderer.getCamera().getYaw() + 180f));
                        Camera camera = mc.gameRenderer.getCamera();
                        matrices.translate(pos.x - camera.getPos().x, pos.y - camera.getPos().y, pos.z - camera.getPos().z);
                        matrices.multiply(mc.getEntityRenderDispatcher().getRotation());
                        int totalWidth = mc.textRenderer.getWidth(epearl.getOwner().getName().getLiteralString());
                        matrices.scale(scaling, -scaling, scaling);

                        RenderUtils.renderQuad(matrices, -totalWidth / 2 - 2, 9, totalWidth / 2 + 2, -2, fillColor.getValue());
                        RenderUtils.renderOutline(matrices, -totalWidth / 2 - 2, 9, totalWidth / 2 + 2, -2, lineColor.getValue());
                        RenderUtils.endRender();
                    }
                }
            }
        }
    }


}

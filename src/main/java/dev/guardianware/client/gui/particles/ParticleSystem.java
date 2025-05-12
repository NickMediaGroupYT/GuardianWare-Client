package dev.guardianware.client.gui.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.guardianware.api.utilities.RenderUtils;
import dev.guardianware.client.modules.client.ModuleParticles;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

import static dev.guardianware.api.utilities.IMinecraft.mc;

public class ParticleSystem {
    public static float SPEED = 0.1f;
    public int dist;
    private final List<Particle> particleList = new ArrayList<>();

    public ParticleSystem(int initAmount, int dist) {
        this.addParticles(initAmount);
        this.dist = dist;
    }

    public void addParticles(int amount) {
        if (mc.getWindow() == null)  return;
        for (int i = 0; i < amount; ++i) {
            this.particleList.add(Particle.generateParticle());
        }
    }

    public void changeParticles(int amount) {
        if (mc.getWindow() == null)  return;
            this.particleList.clear();
        for (int i = 0; i < amount; ++i) {
            this.particleList.add(Particle.generateParticle());
        }
    }

    public void tick(int delta) {
        for (Particle particle : this.particleList) {
            particle.tick(delta, SPEED);
        }
    }

    public void render() {
        for (Particle particle : this.particleList) {
            for (Particle particle1 : this.particleList) {
                float distance = particle.getDistanceTo(particle1);
                if (!(particle.getDistanceTo(particle1) < (float) this.dist)) continue;
                float alpha = Math.min(1.0f, Math.min(1.0f, 1.0f - distance / (float) this.dist));
                RenderUtils.prepare();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableCull();
                RenderSystem.lineWidth(ModuleParticles.INSTANCE.lineWidth.getValue().floatValue());
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
                Matrix4f matrix = new MatrixStack().peek().getPositionMatrix();
                float red = ModuleParticles.INSTANCE.color.getValue().getRed() / 255.0f;
                float green = ModuleParticles.INSTANCE.color.getValue().getGreen() / 255.0f;
                float blue = ModuleParticles.INSTANCE.color.getValue().getBlue() / 255.0f;
                bufferBuilder.vertex(matrix, particle.getX(), particle.getY(), 0.0f).color(red, green, blue, alpha);
                bufferBuilder.vertex(matrix, particle1.getX(), particle1.getY(), 0.0f).color(red, green, blue, alpha);
                BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
                RenderSystem.enableCull();
                RenderSystem.disableBlend();
                RenderUtils.release();
            }
            RenderUtils.drawCircle(particle.getX(), particle.getY(), ModuleParticles.INSTANCE.size.getValue().floatValue(), ModuleParticles.INSTANCE.color.getValue());
        }
    }
}
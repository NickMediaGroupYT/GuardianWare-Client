package dev.guardianware.api.utilities;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

public class RenderUtils implements Util {
    public static Frustum camera = new Frustum(new Matrix4f(), new Matrix4f());

    public static void renderQuad(MatrixStack matrices, float left, float top, float right, float bottom, Color color) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        prepare();

        buffer.vertex(matrix, left, top, 0.0f).color(color.getRGB());
        buffer.vertex(matrix, left, bottom, 0.0f).color(color.getRGB());
        buffer.vertex(matrix, right, bottom, 0.0f).color(color.getRGB());
        buffer.vertex(matrix, right, top, 0.0f).color(color.getRGB());

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        release();
    }

    public static void renderOutline(MatrixStack matrices, float left, float top, float right, float bottom, Color color) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        prepare();

        buffer.vertex(matrix, left, top, 0.0f).color(color.getRGB());
        buffer.vertex(matrix, left, bottom, 0.0f).color(color.getRGB());
        buffer.vertex(matrix, left + 0.5f, bottom, 0.0f).color(color.getRGB());
        buffer.vertex(matrix, left + 0.5f, top, 0.0f).color(color.getRGB());

        buffer.vertex(matrix, right - 0.5f, top, 0.0f).color(color.getRGB());
        buffer.vertex(matrix, right - 0.5f, bottom, 0.0f).color(color.getRGB());
        buffer.vertex(matrix, right, bottom, 0.0f).color(color.getRGB());
        buffer.vertex(matrix, right, top, 0.0f).color(color.getRGB());

        buffer.vertex(matrix, left, bottom - 0.5f, 0.0f).color(color.getRGB());
        buffer.vertex(matrix, left, bottom, 0.0f).color(color.getRGB());
        buffer.vertex(matrix, right, bottom, 0.0f).color(color.getRGB());
        buffer.vertex(matrix, right, bottom - 0.5f, 0.0f).color(color.getRGB());

        buffer.vertex(matrix, left, top, 0.0f).color(color.getRGB());
        buffer.vertex(matrix, left, top + 0.5f, 0.0f).color(color.getRGB());
        buffer.vertex(matrix, right, top + 0.5f, 0.0f).color(color.getRGB());
        buffer.vertex(matrix, right, top, 0.0f).color(color.getRGB());

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        release();
    }

    public static void rect(MatrixStack stack, float x1, float y1, float x2, float y2, int color) {
        rectFilled(stack, x1, y1, x2, y2, color);
    }

    public static void rect(MatrixStack stack, float x1, float y1, float x2, float y2, int color, float width) {
        drawHorizontalLine(stack, x1, x2, y1, color, width);
        drawVerticalLine(stack, x2, y1, y2, color, width);
        drawHorizontalLine(stack, x1, x2, y2, color, width);
        drawVerticalLine(stack, x1, y1, y2, color, width);
    }

    protected static void drawHorizontalLine(MatrixStack matrices, float x1, float x2, float y, int color) {
        if (x2 < x1) {
            float i = x1;
            x1 = x2;
            x2 = i;
        }

        rectFilled(matrices, x1, y, x2 + 1, y + 1, color);
    }

    protected static void drawVerticalLine(MatrixStack matrices, float x, float y1, float y2, int color) {
        if (y2 < y1) {
            float i = y1;
            y1 = y2;
            y2 = i;
        }

        rectFilled(matrices, x, y1 + 1, x + 1, y2, color);
    }

    protected static void drawHorizontalLine(MatrixStack matrices, float x1, float x2, float y, int color, float width) {
        if (x2 < x1) {
            float i = x1;
            x1 = x2;
            x2 = i;
        }

        rectFilled(matrices, x1, y, x2 + width, y + width, color);
    }

    protected static void drawVerticalLine(MatrixStack matrices, float x, float y1, float y2, int color, float width) {
        if (y2 < y1) {
            float i = y1;
            y1 = y2;
            y2 = i;
        }

        rectFilled(matrices, x, y1 + width, x + width, y2, color);
    }

    public static void rectFilled(MatrixStack matrix, float x1, float y1, float x2, float y2, int color) {
        float i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }

        float f = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float j = (float) (color & 255) / 255.0F;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        BufferBuilder bufferBuilder = Tessellator.getInstance()
                .begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix.peek().getPositionMatrix(), x1, y2, 0.0F).color(g, h, j, f);
        bufferBuilder.vertex(matrix.peek().getPositionMatrix(), x2, y2, 0.0F).color(g, h, j, f);
        bufferBuilder.vertex(matrix.peek().getPositionMatrix(), x2, y1, 0.0F).color(g, h, j, f);
        bufferBuilder.vertex(matrix.peek().getPositionMatrix(), x1, y1, 0.0F).color(g, h, j, f);

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    // 3d


    public static void drawBoxFilled(MatrixStack stack, Box box, Color c) {
        float minX = (float) (box.minX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float minY = (float) (box.minY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float minZ = (float) (box.minZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());
        float maxX = (float) (box.maxX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float maxY = (float) (box.maxY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float maxZ = (float) (box.maxZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());

        Tessellator tessellator = Tessellator.getInstance();

        prepare();

        BufferBuilder bufferBuilder = Tessellator.getInstance()
                .begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, maxZ).color(c.getRGB());

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, minZ).color(c.getRGB());

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, minZ).color(c.getRGB());

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, maxZ).color(c.getRGB());

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, maxZ).color(c.getRGB());

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, minZ).color(c.getRGB());

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        release();
    }

    public static void drawBoxFilled(MatrixStack stack, Vec3d vec, Color c) {
        drawBoxFilled(stack, Box.from(vec), c);
    }

    public static void drawBoxFilled(MatrixStack stack, BlockPos bp, Color c) {
        drawBoxFilled(stack, new Box(bp), c);
    }

    public static void drawBox(MatrixStack stack, Box box, Color c, double lineWidth) {
        float minX = (float) (box.minX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float minY = (float) (box.minY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float minZ = (float) (box.minZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());
        float maxX = (float) (box.maxX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float maxY = (float) (box.maxY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float maxZ = (float) (box.maxZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());

        prepare();
        RenderSystem.lineWidth(( float ) lineWidth);
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);

        RenderSystem.defaultBlendFunc();

        BufferBuilder bufferBuilder = Tessellator.getInstance()
                .begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);

        WorldRenderer.drawBox(stack, bufferBuilder, minX, minY, minZ, maxX, maxY, maxZ, c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f);

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        release();
    }

    public static void drawBoxRaw(MatrixStack stack, Box box, Color c, float lineWidth) {
        prepare();
        RenderSystem.lineWidth(lineWidth);
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        RenderSystem.defaultBlendFunc();

        BufferBuilder bufferBuilder = Tessellator.getInstance()
                .begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);

        WorldRenderer.drawBox(stack, bufferBuilder,
                (float) box.minX, (float) box.minY, (float) box.minZ,
                (float) box.maxX, (float) box.maxY, (float) box.maxZ,
                c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f);

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        release();
    }


    public static void drawBox(MatrixStack stack, Vec3d vec, Color c, double lineWidth) {
        drawBox(stack, Box.from(vec), c, lineWidth);
    }

    public static void drawBox(MatrixStack stack, BlockPos bp, Color c, double lineWidth) {
        drawBox(stack, new Box(bp), c, lineWidth);
    }

    public static MatrixStack matrixFrom(Vec3d pos) {
        MatrixStack matrices = new MatrixStack();
        Camera camera = mc.gameRenderer.getCamera();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
        matrices.translate(pos.getX() - camera.getPos().x, pos.getY() - camera.getPos().y, pos.getZ() - camera.getPos().z);
        return matrices;
    }

    public static void setup() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public static void setup3D() {
        setup();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
    }

    public static void clean() {
        RenderSystem.disableBlend();
    }

    public static void clean3D() {
        clean();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
    }


    public static void setupRender() {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,GlStateManager.SrcFactor.ONE,  GlStateManager.DstFactor.ZERO);
        RenderSystem.disableDepthTest();
    }

    public static void endRender() {
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public static void drawTextIn3D(MatrixStack matrices, int x, int y, String text, Vec3d pos, Color fillColor, Boolean shadow) {
        float distance = (float) Math.sqrt(mc.getEntityRenderDispatcher().camera.getPos().squaredDistanceTo(pos.x, pos.y, pos.z));
        float scale = 0.0018f + (30 / 10000.0f) * distance;
        if (distance <= 8.0) scale = 0.0245f;
        setupRender();
        Camera camera = mc.gameRenderer.getCamera();
        VertexConsumerProvider.Immediate vertexConsumers = mc.getBufferBuilders().getEntityVertexConsumers();
        matrices.push();
        matrices.translate(pos.x - camera.getPos().x, pos.y - camera.getPos().y, pos.z - camera.getPos().z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.scale(-scale, -scale, scale);
        mc.textRenderer.draw(text, (float) -mc.textRenderer.getWidth(text) / 2+x, y, fillColor.getRGB(), false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.SEE_THROUGH, 0, 15728880, false);
        matrices.pop();
        endRender();
    }

    public static void prepare() {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(1.0f);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void release() {
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawRect(MatrixStack matrices, float x, float y, float width, float height, Color color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        matrices.push();
        bufferBuilder.vertex(x, height, 0.0f).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        bufferBuilder.vertex(width, height, 0.0f).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        bufferBuilder.vertex(width, y, 0.0f).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        bufferBuilder.vertex(x, y, 0.0f).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        matrices.pop();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
    }

    public static void drawOutline(MatrixStack matrices, float x, float y, float width, float height, float lineWidth, Color color) {
        drawRect(matrices, x + lineWidth, y, x - lineWidth, y + lineWidth, color);
        drawRect(matrices, x + lineWidth, y, width - lineWidth, y + lineWidth, color);
        drawRect(matrices, x, y, x + lineWidth, height, color);
        drawRect(matrices, width - lineWidth, y, width, height, color);
        drawRect(matrices, x + lineWidth, height - lineWidth, width - lineWidth, height, color);
    }

    public static void drawSidewaysGradient(MatrixStack matrices, float x, float y, float width, float height, Color startColor, Color endColor) {
        prepare();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        drawSidewaysPart(bufferBuilder, matrix, x, y, width, height, startColor, endColor);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        release();
    }

    private static void drawSidewaysPart(BufferBuilder bufferBuilder, Matrix4f matrix, float x, float y, float width, float height, Color startColor, Color endColor) {
        bufferBuilder.vertex(matrix, x, y, 0.0f).color(startColor.getRed() / 255.0f, startColor.getGreen() / 255.0f, startColor.getBlue() / 255.0f, startColor.getAlpha() / 255.0f);
        bufferBuilder.vertex(matrix, x + width, y, 0.0f).color(endColor.getRed() / 255.0f, endColor.getGreen() / 255.0f, endColor.getBlue() / 255.0f, endColor.getAlpha() / 255.0f);
        bufferBuilder.vertex(matrix, x + width, y + height, 0.0f).color(endColor.getRed() / 255.0f, endColor.getGreen() / 255.0f, endColor.getBlue() / 255.0f, endColor.getAlpha() / 255.0f);
        bufferBuilder.vertex(matrix, x, y + height, 0.0f).color(startColor.getRed() / 255.0f, startColor.getGreen() / 255.0f, startColor.getBlue() / 255.0f, startColor.getAlpha() / 255.0f);
    }

    public static void drawCircle(float x, float y, float radius, Color color) {
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        RenderSystem.setShaderColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() / 255f);
        double degree = Math.PI / 180;
        for (double i = 0; i <= 90; i += 1) {
            bufferBuilder.vertex((float) (x + (Math.sin(i * degree) * radius)), (float) (y + (Math.cos(i * degree) * radius)), 0.0F);
            bufferBuilder.vertex((float) (x + (Math.sin(i * degree) * radius)), (float) (y - (Math.cos(i * degree) * radius)), 0.0F);
            bufferBuilder.vertex((float) (x - (Math.sin(i * degree) * radius)), (float) (y - (Math.cos(i * degree) * radius)), 0.0F);
            bufferBuilder.vertex((float) (x - (Math.sin(i * degree) * radius)), (float) (y + (Math.cos(i * degree) * radius)), 0.0F);
        }
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public static void drawBlock(BlockPos position, Color color) {
        RenderUtils.drawBlock(new Box(position), color);
    }

    public static void drawBlock(Box bb, Color color) {
        camera.setPosition(Objects.requireNonNull(mc.getCameraEntity()).getX(), mc.getCameraEntity().getY(), mc.getCameraEntity().getZ());
            prepare();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex((float) bb.minX, (float) bb.minY, (float) bb.minZ).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.minY, (float) bb.minZ).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.maxY, (float) bb.minZ).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            bufferBuilder.vertex((float) bb.minX, (float) bb.maxY, (float) bb.minZ).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            bufferBuilder.vertex((float) bb.minX, (float) bb.minY, (float) bb.maxZ).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.minY, (float) bb.maxZ).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.maxY, (float) bb.maxZ).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            bufferBuilder.vertex((float) bb.minX, (float) bb.maxY, (float) bb.maxZ).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            release();
    }

    public static void drawBlockOutline(BlockPos position, Color color, float width) {
        drawBlockOutline(RenderUtils.getRenderBB(position), color, width);
    }

    public static void drawBlockOutline(Box bb, Color color, float width) {
        float red = (float)color.getRed() / 255.0f;
        float green = (float)color.getGreen() / 255.0f;
        float blue = (float)color.getBlue() / 255.0f;
        float alpha = (float)color.getAlpha() / 255.0f;
        camera.setPosition(Objects.requireNonNull(mc.getCameraEntity()).getX(), mc.getCameraEntity().getY(), mc.getCameraEntity().getZ());
        if (camera.isVisible(new Box(bb.minX + mc.getEntityRenderDispatcher().camera.getPos().x, bb.minY + mc.getEntityRenderDispatcher().camera.getPos().y, bb.minZ + mc.getEntityRenderDispatcher().camera.getPos().z, bb.maxX + mc.getEntityRenderDispatcher().camera.getPos().x, bb.maxY + mc.getEntityRenderDispatcher().camera.getPos().y, bb.maxZ + mc.getEntityRenderDispatcher().camera.getPos().z))) {
            prepare();
            RenderSystem.lineWidth(width);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);
            bufferbuilder.vertex((float) bb.minX, (float) bb.minY, (float) bb.minZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.minX, (float) bb.minY, (float) bb.maxZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.maxX, (float) bb.minY, (float) bb.maxZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.maxX, (float) bb.minY, (float) bb.minZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.minX, (float) bb.minY, (float) bb.minZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.minX, (float) bb.maxY, (float) bb.minZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.minX, (float) bb.maxY, (float) bb.maxZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.minX, (float) bb.minY, (float) bb.maxZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.maxX, (float) bb.minY, (float) bb.maxZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.maxX, (float) bb.maxY, (float) bb.maxZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.minX, (float) bb.maxY, (float) bb.maxZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.maxX, (float) bb.maxY, (float) bb.maxZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.maxX, (float) bb.maxY, (float) bb.minZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.maxX, (float) bb.minY, (float) bb.minZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.maxX, (float) bb.maxY, (float) bb.minZ).color(red, green, blue, alpha);
            bufferbuilder.vertex((float) bb.minX, (float) bb.maxY, (float) bb.minZ).color(red, green, blue, alpha);
            BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
            release();
        }
    }

    public static Box getRenderBB(Object position) {
        if (position instanceof BlockPos) {
            return new Box((double)((BlockPos)position).getX() - mc.getEntityRenderDispatcher().camera.getPos().x, (double)((BlockPos)position).getY() - mc.getEntityRenderDispatcher().camera.getPos().y, (double)((BlockPos)position).getZ() - mc.getEntityRenderDispatcher().camera.getPos().z, (double)(((BlockPos)position).getX() + 1) - mc.getEntityRenderDispatcher().camera.getPos().x, (double)(((BlockPos)position).getY() + 1) - mc.getEntityRenderDispatcher().camera.getPos().y, (double)(((BlockPos)position).getZ() + 1) - mc.getEntityRenderDispatcher().camera.getPos().z);
        }
        if (position instanceof Box) {
            return new Box(((Box)position).minX - mc.getEntityRenderDispatcher().camera.getPos().x, ((Box)position).minY - mc.getEntityRenderDispatcher().camera.getPos().y, ((Box)position).minZ - mc.getEntityRenderDispatcher().camera.getPos().z, ((Box)position).maxX - mc.getEntityRenderDispatcher().camera.getPos().x, ((Box)position).maxY - mc.getEntityRenderDispatcher().camera.getPos().y, ((Box)position).maxZ - mc.getEntityRenderDispatcher().camera.getPos().z);
        }
        return null;
    }
}

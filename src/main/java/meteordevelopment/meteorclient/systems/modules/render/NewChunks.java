/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.render;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;

public class NewChunks extends Module {

    private final SettingGroup sgRender = settings.createGroup("Render");

    // Render

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The side color of the rendering.")
        .defaultValue(new SettingColor(0, 255, 0, 75))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The line color of the rendering.")
        .defaultValue(new SettingColor(0, 255, 0, 255))
        .build()
    );

    public NewChunks() {
        super(Categories.Render, "new-chunks", "Renders 1.19 chunks in 2b2t.org.");
    }

    ArrayList<ChunkPos> chunks = new ArrayList<>();

    @EventHandler
    private void onPreTick(TickEvent.Pre event) {
        BlockIterator.register(20, 100, (blockPos, blockState) -> {
            if ((blockState.getBlock().equals(Blocks.COPPER_ORE) && blockPos.getY() > 0 && PlayerUtils.getDimension() == Dimension.Overworld) || ((blockState.getBlock().equals(Blocks.ANCIENT_DEBRIS) && PlayerUtils.getDimension() == Dimension.Nether))) {
                ChunkPos chunkPos = new ChunkPos(blockPos.getX() >> 4, blockPos.getZ() >> 4);
                if (!chunks.contains(chunkPos)) chunks.add(chunkPos);
            }
        });
    }

    @EventHandler
    public void onDeactivate() {
        chunks.clear();
    }

    // Rendering: Thank you to the creators of the Meteor Rejects add-on for the rendering. https://github.com/AntiCope/meteor-rejects/blob/master/src/main/java/anticope/rejects/modules/NewChunks.java

    @EventHandler
    private void onRender(Render3DEvent event) {
        int heightDimension = PlayerUtils.getDimension() == Dimension.Overworld ? -64 : 0;
        for (ChunkPos c : chunks) {
            if (mc.getCameraEntity().getBlockPos().isWithinDistance(c.getStartPos(), 1024)) {
                render(new Box(c.getStartPos()), sideColor.get(), lineColor.get(), shapeMode.get(), event);
            }
        }
    }

    private void render(Box box, Color sides, Color lines, ShapeMode shapeMode, Render3DEvent event) {
        event.renderer.box(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, sides, lines, shapeMode, 0);
    }

}

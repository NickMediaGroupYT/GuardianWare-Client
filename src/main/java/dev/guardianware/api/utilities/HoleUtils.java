package dev.guardianware.api.utilities;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class HoleUtils implements IMinecraft {
    public static boolean isBedrockHole(BlockPos pos) {
        boolean retVal = false;
        if (mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.up().up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.down()).getBlock().equals(Blocks.BEDROCK) && mc.world.getBlockState(pos.east()).getBlock().equals(Blocks.BEDROCK) && mc.world.getBlockState(pos.west()).getBlock().equals(Blocks.BEDROCK) && mc.world.getBlockState(pos.south()).getBlock().equals(Blocks.BEDROCK) && mc.world.getBlockState(pos.north()).getBlock().equals(Blocks.BEDROCK)) {
            retVal = true;
        }
        return retVal;
    }

    public static boolean isObiHole(BlockPos pos) {
        boolean retVal = false;
        int obiCount = 0;
        if (mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.up().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.down()).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos.down()).getBlock().equals(Blocks.OBSIDIAN))) {
            if (mc.world.getBlockState(pos.down()).getBlock().equals(Blocks.OBSIDIAN)) {
                ++obiCount;
            }
            if (mc.world.getBlockState(pos.east()).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos.east()).getBlock().equals(Blocks.OBSIDIAN)) {
                if (mc.world.getBlockState(pos.east()).getBlock().equals(Blocks.OBSIDIAN)) {
                    ++obiCount;
                }
                if (mc.world.getBlockState(pos.west()).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos.west()).getBlock().equals(Blocks.OBSIDIAN)) {
                    if (mc.world.getBlockState(pos.west()).getBlock().equals(Blocks.OBSIDIAN)) {
                        ++obiCount;
                    }
                    if (mc.world.getBlockState(pos.south()).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos.south()).getBlock().equals(Blocks.OBSIDIAN)) {
                        if (mc.world.getBlockState(pos.south()).getBlock().equals(Blocks.OBSIDIAN)) {
                            ++obiCount;
                        }
                        if (mc.world.getBlockState(pos.north()).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos.north()).getBlock().equals(Blocks.OBSIDIAN)) {
                            if (mc.world.getBlockState(pos.north()).getBlock().equals(Blocks.OBSIDIAN)) {
                                ++obiCount;
                            }
                            if (obiCount >= 1) {
                                retVal = true;
                            }
                        }
                    }
                }
            }
        }
        return retVal;
    }

    public static boolean isDoubleHole(BlockPos pos) {
        for (Direction f : Direction.Type.HORIZONTAL) {
            int offZ;
            int offX = f.getOffsetX();
            if (mc.world.getBlockState(pos.add(offX, 0, offZ = f.getOffsetZ())).getBlock() != Blocks.OBSIDIAN && mc.world.getBlockState(pos.add(offX, 0, offZ)).getBlock() != Blocks.BEDROCK || mc.world.getBlockState(pos.add(offX * -2, 0, offZ * -2)).getBlock() != Blocks.OBSIDIAN && mc.world.getBlockState(pos.add(offX * -2, 0, offZ * -2)).getBlock() != Blocks.BEDROCK || mc.world.getBlockState(pos.add(offX * -1, 0, offZ * -1)).getBlock() != Blocks.AIR || !isSafeBlock(pos.add(0, -1, 0)) || !isSafeBlock(pos.add(offX * -1, -1, offZ * -1))) continue;
            if (offZ == 0 && isSafeBlock(pos.add(0, 0, 1)) && isSafeBlock(pos.add(0, 0, -1)) && isSafeBlock(pos.add(offX * -1, 0, 1)) && isSafeBlock(pos.add(offX * -1, 0, -1))) {
                return true;
            }
            if (offX != 0 || !isSafeBlock(pos.add(1, 0, 0)) || !isSafeBlock(pos.add(-1, 0, 0)) || !isSafeBlock(pos.add(1, 0, offZ * -1)) || !isSafeBlock(pos.add(-1, 0, offZ * -1))) continue;
            return true;
        }
        return false;
    }

    public static boolean isHole(BlockPos pos) {
        boolean retVal = false;
        if (mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.up().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.down()).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos.down()).getBlock().equals(Blocks.OBSIDIAN)) && (mc.world.getBlockState(pos.east()).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos.east()).getBlock().equals(Blocks.OBSIDIAN)) && (mc.world.getBlockState(pos.west()).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos.west()).getBlock().equals(Blocks.OBSIDIAN)) && (mc.world.getBlockState(pos.south()).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos.south()).getBlock().equals(Blocks.OBSIDIAN)) && (mc.world.getBlockState(pos.north()).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos.north()).getBlock().equals(Blocks.OBSIDIAN))) {
            retVal = true;
        }
        return retVal;
    }

    static boolean isSafeBlock(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK;
    }

    public static boolean isDoubleBedrockHoleX(BlockPos blockPos) {
        if (!mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(1, 1, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(1, 2, 0)).getBlock().equals(Blocks.AIR)) {
            return false;
        }
        for (BlockPos blockPos2 : new BlockPos[]{blockPos.add(2, 0, 0), blockPos.add(1, 0, 1), blockPos.add(1, 0, -1), blockPos.add(-1, 0, 0), blockPos.add(0, 0, 1), blockPos.add(0, 0, -1), blockPos.add(0, -1, 0), blockPos.add(1, -1, 0)}) {
            BlockState iBlockState = mc.world.getBlockState(blockPos2);
            if (iBlockState.getBlock() != Blocks.AIR && iBlockState.getBlock() == Blocks.BEDROCK) continue;
            return false;
        }
        return true;
    }

    public static boolean isDoubleBedrockHoleZ(BlockPos blockPos) {
        if (!mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(0, 1, 1)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(0, 2, 1)).getBlock().equals(Blocks.AIR)) {
            return false;
        }
        for (BlockPos blockPos2 : new BlockPos[]{blockPos.add(0, 0, 2), blockPos.add(1, 0, 1), blockPos.add(-1, 0, 1), blockPos.add(0, 0, -1), blockPos.add(1, 0, 0), blockPos.add(-1, 0, 0), blockPos.add(0, -1, 0), blockPos.add(0, -1, 1)}) {
            BlockState iBlockState = mc.world.getBlockState(blockPos2);
            if (iBlockState.getBlock() != Blocks.AIR && iBlockState.getBlock() == Blocks.BEDROCK) continue;
            return false;
        }
        return true;
    }

    public static boolean isDoubleObsidianHoleX(BlockPos blockPos) {
        if (!mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(1, 1, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(1, 2, 0)).getBlock().equals(Blocks.AIR)) {
            return false;
        }
        for (BlockPos blockPos2 : new BlockPos[]{blockPos.add(2, 0, 0), blockPos.add(1, 0, 1), blockPos.add(1, 0, -1), blockPos.add(-1, 0, 0), blockPos.add(0, 0, 1), blockPos.add(0, 0, -1), blockPos.add(0, -1, 0), blockPos.add(1, -1, 0)}) {
            if (BlockUtils.getBlockResistance(blockPos2) == BlockUtils.BlockResistance.Resistant || BlockUtils.getBlockResistance(blockPos2) == BlockUtils.BlockResistance.Unbreakable) continue;
            return false;
        }
        return true;
    }

    public static boolean isDoubleObsidianHoleZ(BlockPos blockPos) {
        if (!mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(0, 1, 1)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(0, 2, 1)).getBlock().equals(Blocks.AIR)) {
            return false;
        }
        for (BlockPos blockPos2 : new BlockPos[]{blockPos.add(0, 0, 2), blockPos.add(1, 0, 1), blockPos.add(-1, 0, 1), blockPos.add(0, 0, -1), blockPos.add(1, 0, 0), blockPos.add(-1, 0, 0), blockPos.add(0, -1, 0), blockPos.add(0, -1, 1)}) {
            if (BlockUtils.getBlockResistance(blockPos2) == BlockUtils.BlockResistance.Resistant || BlockUtils.getBlockResistance(blockPos2) == BlockUtils.BlockResistance.Unbreakable) continue;
            return false;
        }
        return true;
    }

    public static boolean isInHole(PlayerEntity player) {
        boolean retVal = false;
        BlockPos pos = new BlockPos((int) Math.floor(player.getX()), (int) player.getY(), (int) Math.floor(player.getZ()));
        if (mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.down()).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos.down()).getBlock().equals(Blocks.OBSIDIAN)) && (mc.world.getBlockState(pos.east()).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos.east()).getBlock().equals(Blocks.OBSIDIAN)) && (mc.world.getBlockState(pos.west()).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos.west()).getBlock().equals(Blocks.OBSIDIAN)) && (mc.world.getBlockState(pos.south()).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos.south()).getBlock().equals(Blocks.OBSIDIAN)) && (mc.world.getBlockState(pos.north()).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos.north()).getBlock().equals(Blocks.OBSIDIAN))) {
            retVal = true;
        }
        return retVal;
    }
}

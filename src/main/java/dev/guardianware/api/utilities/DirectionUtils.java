package dev.guardianware.api.utilities;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.List;

import static dev.guardianware.api.utilities.IMinecraft.mc;

public class DirectionUtils {

    public static Direction getDirection(BlockPos pos) {
        Vec3d eyesPos = new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());
        if ((double) pos.getY() > eyesPos.y) {
            if (mc.world.getBlockState(pos.add(0, -1, 0)).isReplaceable()) return Direction.DOWN;
            else return mc.player.getHorizontalFacing().getOpposite();
        }
        if (!mc.world.getBlockState(pos.add(0, 1, 0)).isReplaceable()) return mc.player.getHorizontalFacing().getOpposite();
        return Direction.UP;
    }

    public enum EightWayDirections {
        NORTH(Direction.NORTH),
        SOUTH(Direction.SOUTH),
        EAST(Direction.EAST),
        WEST(Direction.WEST),
        NORTHEAST(Direction.NORTH, Direction.EAST),
        NORTHWEST(Direction.NORTH, Direction.WEST),
        SOUTHEAST(Direction.SOUTH, Direction.EAST),
        SOUTHWEST(Direction.SOUTH, Direction.WEST);

        private final List<Direction> directions;

        EightWayDirections(Direction... directions) {
            this.directions = Arrays.asList(directions);
        }

        public BlockPos offset(BlockPos pos) {
            BlockPos result = pos;
            for (Direction direction : directions) {
                result = result.offset(direction);
            }
            return result;
        }
    }

}

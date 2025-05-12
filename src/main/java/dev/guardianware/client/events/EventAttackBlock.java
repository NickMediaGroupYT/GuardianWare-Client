package dev.guardianware.client.events;

import dev.guardianware.api.manager.event.EventArgument;
import dev.guardianware.api.manager.event.EventListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class EventAttackBlock extends EventArgument {
    private BlockPos pos;
    private Direction direction;
    public EventAttackBlock(BlockPos pos, Direction direction) {
        this.pos = pos;
        this.direction = direction;
    }

    @Override
    public void call(EventListener listener) {
        listener.onAttackBlock(this);
    }

    public BlockPos getPos() {
        return pos;
    }
    public Direction getDirection() {
        return direction;
    }

}

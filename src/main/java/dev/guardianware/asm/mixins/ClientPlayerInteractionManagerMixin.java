package dev.guardianware.asm.mixins;

import dev.guardianware.client.events.EventAttackBlock;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.guardianware.GuardianWare;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method = "attackBlock", at = @At("HEAD"), cancellable = true)
    private void attackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> info) {
        EventAttackBlock event = new EventAttackBlock(pos, direction);
        GuardianWare.EVENT_MANAGER.call(event);
        if (event.isCanceled()) {
            info.setReturnValue(false);
        }
    }
}

package dev.guardianware.asm.mixins;

import dev.guardianware.GuardianWare;
import dev.guardianware.client.modules.visuals.NoRender;
import net.minecraft.client.particle.ExplosionEmitterParticle;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
    @Inject(at = @At("HEAD"), method = "addParticle(Lnet/minecraft/client/particle/Particle;)V", cancellable = true)
    public void addParticleHook(Particle p, CallbackInfo e) {
        if (p instanceof ExplosionLargeParticle || p instanceof ExplosionEmitterParticle) {
            if (GuardianWare.MODULE_MANAGER.getInstance(NoRender.class).explosions.getValue()) {
                e.cancel();
            }
        }
    }
}

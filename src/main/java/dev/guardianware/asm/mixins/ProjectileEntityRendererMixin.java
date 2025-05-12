package dev.guardianware.asm.mixins;


import dev.guardianware.GuardianWare;
import dev.guardianware.client.modules.visuals.NoRender;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntityRenderer.class) // Target ProjectileEntityRenderer
public abstract class ProjectileEntityRendererMixin<T extends PersistentProjectileEntity> {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(T entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (GuardianWare.MODULE_MANAGER.getInstance(NoRender.class).arrows.getValue()) {
            ci.cancel();
        }
    }
}

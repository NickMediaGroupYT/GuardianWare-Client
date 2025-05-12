package dev.guardianware.client.modules.combat;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.api.utilities.RotationUtils;
import dev.guardianware.client.events.Render3DEvent;
import dev.guardianware.client.values.impl.ValueNumber;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

@RegisterModule(name="AimBot", tag="AimBot", description="Automatically set crosshair to player.", category= Module.Category.COMBAT)
public class AimBot extends Module {
    final ValueNumber distance = new ValueNumber("Distance", "Distance", "", 5.0, 0.0, 10.0);

    @Override
    public void onRender3D(Render3DEvent event) {
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || getDistanceTo(player)>distance.getValue().doubleValue()) continue;
                float[] rot = RotationUtils.getRotationsEntity(player);
                mc.player.setAngles(rot[0],rot[1]);
        }
    }

    public static double getDistanceTo(Entity to) {
        return mc.player.getPos().distanceTo(to.getPos());
    }


}

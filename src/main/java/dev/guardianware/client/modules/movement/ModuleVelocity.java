package dev.guardianware.client.modules.movement;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.asm.mixins.IEntityVelocityUpdateS2CPacket;
import dev.guardianware.asm.mixins.IExplosionS2CPacket;
import dev.guardianware.client.events.EventPacketReceive;
import dev.guardianware.client.events.EventPush;
import dev.guardianware.client.values.impl.ValueBoolean;
import dev.guardianware.client.values.impl.ValueNumber;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.Formatting;

@RegisterModule(name="Velocity", description="Remove the knockback of the player.", category=Module.Category.MOVEMENT)
public class ModuleVelocity extends Module {
    public static ValueBoolean noPush = new ValueBoolean("NoPush", "NoPush", "", false);
    public static ValueNumber horizontal = new ValueNumber("Horizontal", "Horizontal", "", 0.0f, 0.0f, 100.0f);
    public static ValueNumber vertical = new ValueNumber("Vertical", "Vertical", "", 0.0f, 0.0f, 100.0f);
    public static ValueBoolean wallsOnly = new ValueBoolean("WallsOnly", "WallsOnly", "", true);

    @Override
    public void onPacketReceive(EventPacketReceive event) {
        EntityVelocityUpdateS2CPacket sPacketEntityVelocity;
        if (mc.player == null || mc.world == null || (!mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox()).iterator().hasNext()) && wallsOnly.getValue()) {
            return;
        }
        if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket && (sPacketEntityVelocity = (EntityVelocityUpdateS2CPacket) event.getPacket()).getEntityId() == mc.player.getId()) {
            if (horizontal.getValue().floatValue() == 0.0f && vertical.getValue().floatValue() == 0.0f) {
                event.cancel();
            } else {
                ((IEntityVelocityUpdateS2CPacket) sPacketEntityVelocity).setX((int) (sPacketEntityVelocity.getVelocityX() * horizontal.getValue().floatValue() / 100));
                ((IEntityVelocityUpdateS2CPacket) sPacketEntityVelocity).setY((int) (sPacketEntityVelocity.getVelocityY() * vertical.getValue().floatValue() / 100));
                ((IEntityVelocityUpdateS2CPacket) sPacketEntityVelocity).setZ((int) (sPacketEntityVelocity.getVelocityZ() * horizontal.getValue().floatValue() / 100));
            }
        }
        if (event.getPacket() instanceof ExplosionS2CPacket sPacketExplosion) {
                ((IExplosionS2CPacket) sPacketExplosion).setX((int) (sPacketExplosion.getPlayerVelocityX() * horizontal.getValue().floatValue() / 100));
                ((IExplosionS2CPacket) sPacketExplosion).setY((int) (sPacketExplosion.getPlayerVelocityY() * vertical.getValue().floatValue() / 100));
                ((IExplosionS2CPacket) sPacketExplosion).setZ((int) (sPacketExplosion.getPlayerVelocityZ() * horizontal.getValue().floatValue() / 100));
        }
    }

    @Override
    public void onPush(EventPush event) {
        if (noPush.getValue()) {
            event.cancel();
        }
    }

    @Override
    public String getHudInfo() {
        return "H" + horizontal.getValue().floatValue() + "%" + Formatting.GRAY + "," + Formatting.WHITE + "V" + vertical.getValue().floatValue() + "%";
    }
}

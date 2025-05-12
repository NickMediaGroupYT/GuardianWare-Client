package dev.guardianware.client.modules.movement;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.client.events.EventRender2D;
import dev.guardianware.client.values.impl.ValueBoolean;
import dev.guardianware.client.values.impl.ValueEnum;
import dev.guardianware.client.values.impl.ValueNumber;
import net.minecraft.block.Blocks;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.lwjgl.glfw.GLFW;

@RegisterModule(name = "ElytraBounce", tag = "ElytraBounce", description = "Packet-based Elytra bounce with void protection and hover modes", category = Module.Category.MOVEMENT)
public class ElytraBounce extends Module {

    private final ValueEnum mode = new ValueEnum("Mode", "Mode", "Operation mode", ElytraModes.Bounce);
    private enum ElytraModes {
        Bounce,
        Hover,
        VoidRescue;
    }

    private final ValueNumber bounceHeight = new ValueNumber("BounceHeight", "BounceHeight", "Spoofed upward motion", 0.42, 0.1, 1.0);
    private final ValueNumber triggerDistance = new ValueNumber("TriggerY", "TriggerY", "Trigger bounce near ground", 0.6, 0.1, 2.0);
    private final ValueNumber cooldownTicks = new ValueNumber("Cooldown", "Cooldown", "Delay between bounces", 5, 1, 20);
    private final ValueNumber hoverInterval = new ValueNumber("HoverInterval", "HoverInterval", "Ticks between hover", 10, 1, 40);

    private final ValueNumber voidY = new ValueNumber("VoidY", "VoidY", "Trigger VoidRescue below this Y", 50, 0, 128);
    private final ValueBoolean requireElytra = new ValueBoolean("ElytraOnly", "ElytraOnly", "Only works with Elytra equipped", true);
    private final ValueBoolean desyncSpoof = new ValueBoolean("DesyncSpoof", "DesyncSpoof", "Send extra desync packets", true);
    private final ValueBoolean debugHUD = new ValueBoolean("DebugHUD", "DebugHUD", "Show debug overlay", true);

    private final KeyBinding modeSwitchKey = new KeyBinding("ElytraBounce Mode Switch", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, "GuardianWare");

    private int cooldown = 0;
    private int hoverTicks = 0;

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        if (modeSwitchKey.wasPressed()) {
            cycleMode();
        }

        if (!mc.player.isFallFlying()) {
            log("Not gliding.");
            return;
        }

        if (requireElytra.getValue() && !mc.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)) {
            log("Elytra not equipped.");
            return;
        }

        if (cooldown > 0) cooldown--;

        if (mode.getValue().equals("Bounce")) {
            if (cooldown == 0 && isNearGround(triggerDistance.getValue().doubleValue())) {
                log("Bouncing: near ground.");
                spoofVerticalMotion(bounceHeight.getValue().doubleValue());
                cooldown = cooldownTicks.getValue().intValue();
            }
        } else if (mode.getValue().equals("Hover")) {
            hoverTicks++;
            if (hoverTicks >= hoverInterval.getValue().intValue()) {
                log("Hover tick.");
                spoofVerticalMotion(0.0001);
                hoverTicks = 0;
            }
        } else if (mode.getValue().equals("VoidRescue")) {
            if (cooldown == 0 && (mc.player.getY() <= voidY.getValue().doubleValue() || isAboveVoid())) {
                log("Void rescue triggered.");
                spoofVerticalMotion(bounceHeight.getValue().doubleValue());
                cooldown = cooldownTicks.getValue().intValue();
            }
        }
    }

    public void onRender2D() {
        if (!debugHUD.getValue() || mc.player == null) return;

        String status = "[ElytraBounce] Mode: " + mode.getValue()
                + " | Y: " + String.format("%.2f", mc.player.getY())
                + " | Flying: " + mc.player.isFallFlying()
                + " | Void: " + isAboveVoid();

        mc.textRenderer.draw(status, 10, 10, 0xFF55FF55, true, null, null, null, 0, 0, false);
    }

    private void spoofVerticalMotion(double yOffset) {
        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();

        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + yOffset, z, false));

        if (desyncSpoof.getValue()) {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + yOffset - 0.05, z, false));
        }

        log("Spoofed Y motion by " + yOffset);
    }

    private boolean isNearGround(double distance) {
        Box box = mc.player.getBoundingBox().offset(0, -distance, 0);
        return mc.world.getBlockCollisions(mc.player, box).iterator().hasNext();
    }

    private boolean isAboveVoid() {
        BlockPos.Mutable pos = new BlockPos.Mutable(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        for (int y = (int) mc.player.getY(); y >= 0; y--) {
            pos.setY(y);
            if (!mc.world.isChunkLoaded(pos)) return true;
            if (!mc.world.getBlockState(pos).isOf(Blocks.AIR)) return false;
        }
        return true;
    }

    private void cycleMode() {
        String current = String.valueOf(mode.getValue());
        String[] options = mode.getModes();
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(current)) {
                String next = options[(i + 1) % options.length];
                mode.setValue(next);
                mc.player.sendMessage(new LiteralText("§d[ElytraBounce] Mode set to §f" + next), false);
                return;
            }
        }
    }

    private void log(String message) {
        System.out.println("[ElytraBounce] " + message);
    }
}
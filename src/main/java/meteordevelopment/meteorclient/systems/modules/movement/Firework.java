/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Firework extends Module {
    private static final int BOOST_DURATION = 30;
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgVelocity = settings.createGroup("Velocity");
    private final Setting<Boolean> onlyWhenHoldingRocket = sgGeneral.add(new BoolSetting.Builder()
        .name("only-when-holding")
        .description("Only auto-fire when holding a rocket.")
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> checkCooldown = sgGeneral.add(new BoolSetting.Builder()
        .name("check-cooldown")
        .description("Check item use cooldown before firing.")
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> onlyWhenFlying = sgGeneral.add(new BoolSetting.Builder()
        .name("only-when-flying")
        .description("Only fire rockets when using elytra.")
        .defaultValue(true)
        .build()
    );
    private final Setting<Double> useDelay = sgGeneral.add(new DoubleSetting.Builder()
        .name("use-delay")
        .description("Delay in seconds between using rockets.")
        .defaultValue(.1)
        .min(0.1)
        .sliderMax(5.0)
        .build()
    );
    private final Setting<Double> minSpeed = sgVelocity.add(new DoubleSetting.Builder()
        .name("minimum-speed")
        .description("Minimum speed threshold before using rockets.")
        .defaultValue(1.35)
        .min(0.1)
        .sliderMax(2.0)
        .build()
    );
    private boolean isBoostActive = false;
    private int boostTicks = 0;
    private Vec3d lastPos = Vec3d.ZERO;
    private int ticksSinceLastUse = 0;

    public Firework() {
        super(Categories.Movement, "Firework", "Automatically fires rockets when boost ends.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null) return;

        ticksSinceLastUse++;

        if (isBoostActive) {
            boostTicks++;
            if (boostTicks >= BOOST_DURATION) {
                isBoostActive = false;
                boostTicks = 0;
            }
        }

        if (onlyWhenFlying.get() && !mc.player.isFallFlying()) {
            isBoostActive = false;
            return;
        }

        Vec3d currentPos = mc.player.getPos();
        Vec3d velocity = currentPos.subtract(lastPos);
        lastPos = currentPos;

        float yaw = mc.player.getYaw();
        float pitch = mc.player.getPitch();
        Vec3d lookVec = getLookVector(yaw, pitch);

        double forwardSpeed = velocity.dotProduct(lookVec);
        int delayTicks = (int) (useDelay.get() * 20);

        if (!isBoostActive && forwardSpeed < minSpeed.get() && ticksSinceLastUse >= delayTicks) {
            // Check if player is holding a rocket
            boolean holdingRocket = mc.player.getMainHandStack().getItem() == Items.FIREWORK_ROCKET ||
                mc.player.getOffHandStack().getItem() == Items.FIREWORK_ROCKET;
            // moving fw inventory pull logic to new module
            if (!holdingRocket) {
                int slot = -1;
                for (int i = 0; i < 9; i++) {
                    if (mc.player.getInventory().getStack(i).getItem() == Items.FIREWORK_ROCKET) {
                        slot = i;
                        break;
                    }
                }
                if (slot == -1) {
                    for (int i = 9; i < mc.player.getInventory().size(); i++) {
                        if (mc.player.getInventory().getStack(i).getItem() == Items.FIREWORK_ROCKET) {
                            for (int j = 0; j < 9; j++) {
                                if (mc.player.getInventory().getStack(j).isEmpty()) {
                                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i + 36, j,
                                        net.minecraft.screen.slot.SlotActionType.SWAP, mc.player);
                                    slot = j;
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }

                holdingRocket = mc.player.getMainHandStack().getItem() == Items.FIREWORK_ROCKET ||
                    mc.player.getOffHandStack().getItem() == Items.FIREWORK_ROCKET;
                if (!holdingRocket && onlyWhenHoldingRocket.get()) return;
            }
            if (checkCooldown.get() && mc.player.getItemCooldownManager().isCoolingDown(Items.FIREWORK_ROCKET)) return;

            Hand hand = mc.player.getMainHandStack().getItem() == Items.FIREWORK_ROCKET
                ? Hand.MAIN_HAND
                : Hand.OFF_HAND;

            mc.interactionManager.interactItem(mc.player, hand);
            mc.player.swingHand(hand);
            isBoostActive = true;
            boostTicks = 0;
            ticksSinceLastUse = 0;
        }
    }

    private Vec3d getLookVector(float yaw, float pitch) {
        float f = pitch * 0.017453292F;
        float g = -yaw * 0.017453292F;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j).normalize();
    }

    @Override
    public void onActivate() {
        lastPos = mc.player != null ? mc.player.getPos() : Vec3d.ZERO;
        isBoostActive = false;
        boostTicks = 0;
        ticksSinceLastUse = 0;
    }

    @Override
    public void onDeactivate() {
        isBoostActive = false;
        boostTicks = 0;
        ticksSinceLastUse = 0;
    }
}

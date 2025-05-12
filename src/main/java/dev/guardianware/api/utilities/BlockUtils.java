package dev.guardianware.api.utilities;

import dev.guardianware.GuardianWare;
import dev.guardianware.api.manager.rotation.RotationManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShapes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockUtils implements IMinecraft {

    public static boolean placeBlock(BlockPos position, Hand hand, boolean rotate) {
        if (!mc.world.getBlockState(position).canReplace(new ItemPlacementContext(mc.player, Hand.MAIN_HAND, mc.player.getStackInHand(Hand.MAIN_HAND), new BlockHitResult(Vec3d.of(position), Direction.UP, position, false)))) {
            return rotate;
        }
        if (getPlaceableSide(position) == null) {
            return rotate;
        }
        if (rotate) {
            float[] rot = RotationManager.getRotationsTo(position);
            GuardianWare.ROTATION.requestRotation(50, rot[0], rot[1], true);
        }
        mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, new BlockHitResult(Vec3d.of(position.offset(Objects.requireNonNull(getPlaceableSide(position)))), Objects.requireNonNull(getPlaceableSide(position)).getOpposite(), position.offset(Objects.requireNonNull(getPlaceableSide(position))), false), 0));
        mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(hand));
        return rotate;
    }

    public static boolean isPositionPlaceable(BlockPos position, boolean entityCheck, boolean sideCheck) {
        if (!mc.world.getBlockState(position).getBlock().canReplace(mc.world.getBlockState(position), new ItemPlacementContext(mc.player, Hand.MAIN_HAND, mc.player.getStackInHand(Hand.MAIN_HAND), new BlockHitResult(Vec3d.of(position), Direction.UP, position, false)))) {
            return false;
        }
        if (entityCheck) {
            for (Entity entity : mc.world.getEntitiesByClass(Entity.class, new Box(position), Entity::isAlive)) {
                if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity) continue;
                return false;
            }
        }
        if (sideCheck) {
            return getPlaceableSide(position) != null;
        }
        return true;
    }

    public static boolean isPositionPlaceable(BlockPos position, boolean entityCheck, boolean sideCheck, boolean ignoreCrystals) {
        if (!mc.world.getBlockState(position).getBlock().canReplace(mc.world.getBlockState(position), new ItemPlacementContext(mc.player, Hand.MAIN_HAND, mc.player.getStackInHand(Hand.MAIN_HAND), new BlockHitResult(Vec3d.of(position), Direction.UP, position, false)))) {
            return false;
        }
        if (entityCheck) {
            for (Entity entity : mc.world.getEntitiesByClass(Entity.class, new Box(position), Entity::isAlive)) {
                if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof EndCrystalEntity && ignoreCrystals) continue;
                return false;
            }
        }
        if (sideCheck) {
            return getPlaceableSide(position) != null;
        }
        return true;
    }

    public static Direction getPlaceableSide(BlockPos position) {
        for (Direction side : Direction.values()) {
            if (!mc.world.getBlockState(position.offset(side)).blocksMovement() || mc.world.getBlockState(position.offset(side)).isLiquid()) continue;
            return side;
        }
        return null;
    }

    public static List<BlockPos> getNearbyBlocks(PlayerEntity player, double blockRange, boolean motion) {
        ArrayList<BlockPos> nearbyBlocks = new ArrayList<>();
        int range = (int)MathUtils.roundToPlaces(blockRange, 0);
        if (motion) {
            player.getPos().add(Vec3d.of(new Vec3i((int) player.getVelocity().x, (int) player.getVelocity().y, (int) player.getVelocity().z)));
        }
        for (int x = -range; x <= range; ++x) {
            for (int y = -range; y <= range - range / 2; ++y) {
                for (int z = -range; z <= range; ++z) {
                    nearbyBlocks.add(BlockPos.ofFloored(player.getPos().add(x, y, z)));
                }
            }
        }
        return nearbyBlocks;
    }

    public static BlockResistance getBlockResistance(BlockPos block) {
        if (mc.world.isAir(block)) {
            return BlockResistance.Blank;
        }
        if (!(mc.world.getBlockState(block).getBlock().getHardness() == -1.0f || mc.world.getBlockState(block).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(block).getBlock().equals(Blocks.ANVIL) || mc.world.getBlockState(block).getBlock().equals(Blocks.ENCHANTING_TABLE) || mc.world.getBlockState(block).getBlock().equals(Blocks.ENDER_CHEST))) {
            return BlockResistance.Breakable;
        }
        if (mc.world.getBlockState(block).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(block).getBlock().equals(Blocks.ANVIL) || mc.world.getBlockState(block).getBlock().equals(Blocks.ENCHANTING_TABLE) || mc.world.getBlockState(block).getBlock().equals(Blocks.ENDER_CHEST)) {
            return BlockResistance.Resistant;
        }
        if (mc.world.getBlockState(block).getBlock().equals(Blocks.BEDROCK)) {
            return BlockResistance.Unbreakable;
        }
        return null;
    }

    public enum BlockResistance {
        Blank,
        Breakable,
        Resistant,
        Unbreakable
    }

    public static boolean canBreak(BlockPos blockPos, BlockState state) {
        if (!mc.player.isCreative() && state.getHardness(mc.world, blockPos) < 0 && state.getBlock() != Blocks.BEDROCK) return false;
        return state.getOutlineShape(mc.world, blockPos) != VoxelShapes.empty();
    }

    public static boolean canBreak(BlockPos blockPos) {
        return canBreak(blockPos, mc.world.getBlockState(blockPos));
    }

    public static double getBlockBreakingSpeed(int slot, BlockState block, boolean isOnGround) {
        double speed = mc.player.getInventory().main.get(slot).getMiningSpeedMultiplier(block);

        if (speed > 1) {
            ItemStack tool = mc.player.getInventory().getStack(slot);

            int efficiency = InventoryUtils.getEnchantmentLevel(tool, Enchantments.EFFICIENCY);

            if (efficiency > 0 && !tool.isEmpty()) speed += efficiency * efficiency + 1;
        }

        if (StatusEffectUtil.hasHaste(mc.player)) {
            speed *= 1 + (StatusEffectUtil.getHasteAmplifier(mc.player) + 1) * 0.2F;
        }

        if (mc.player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            float k = switch (mc.player.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) {
                case 0 -> 0.3F;
                case 1 -> 0.09F;
                case 2 -> 0.0027F;
                default -> 8.1E-4F;
            };

            speed *= k;
        }

        if (mc.player.isSubmergedIn(FluidTags.WATER)) {
            speed *= mc.player.getAttributeValue(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED);
        }

        if (!isOnGround) {
            speed /= 5.0F;
        }

        return speed;
    }
}

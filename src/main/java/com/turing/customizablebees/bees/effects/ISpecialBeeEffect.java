package com.turing.customizablebees.bees.effects;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public interface ISpecialBeeEffect {
    float getCooldown(IBeeGenome genome, Random rand);

    default boolean canTravelThroughWalls() {
        return false;
    }

    interface SpecialEffectBlock extends ISpecialBeeEffect {
        boolean canHandleBlock(World world, BlockPos pos, IBeeGenome genome, EnumFacing sideHit);

        boolean handleBlock(World world, BlockPos pos, EnumFacing facing, IBeeGenome genome, IBeeHousing housing);

        default boolean includeAirBlocks() {
            return false;
        }

        default void processingTick(World world, BlockPos pos, IBeeGenome genome, IBeeHousing housing, EnumFacing facing) {

        }

        default float getCooldown(World world, BlockPos pos, IBeeGenome genome, EnumFacing facing, Random rand) {
            return getCooldown(genome, rand);
        }
    }

    interface SpecialEffectEntity extends ISpecialBeeEffect {
        boolean canHandleEntity(Entity entity, IBeeGenome genome);

        boolean handleEntity(Entity entity, IBeeGenome genome, IBeeHousing housing);

        default void processingTick(Entity entity, IBeeGenome genome, IBeeHousing housing) {

        }

        default float getCooldown(Entity entity, IBeeGenome genome, Random rand) {
            return getCooldown(genome, rand);
        }
    }

    interface SpecialEffectItem extends ISpecialBeeEffect {
        boolean canHandleStack(ItemStack stack, IBeeGenome genome);

        @Nullable
        ItemStack handleStack(ItemStack stack, IBeeGenome genome, IBeeHousing housing);
    }
}

package com.turing.customizablebees.bees.effects;

import com.turing.customizablebees.bees.effects.settings.IEffectSettingsHolder;
import com.turing.customizablebees.network.DoParticlePacket;
import com.turing.customizablebees.network.Messages;
import com.turing.customizablebees.util.ParticleHelper;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.genetics.IEffectData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public abstract class WorldInteractionEffect extends ThrottledEffectBase implements ISpecialBeeEffect.SpecialEffectBlock {
    public WorldInteractionEffect(String modid, String name, float baseTicksBetweenProcessing) {
        this(modid, name, baseTicksBetweenProcessing, 1);
    }

    public WorldInteractionEffect(String modid, String name, float baseTicksBetweenProcessing, float chanceOfProcessing) {
        this(modid, name, false, false, baseTicksBetweenProcessing, chanceOfProcessing);
    }

    public WorldInteractionEffect(String modid, String name, boolean isDominant, boolean isCombinable, float baseTicksBetweenProcessing, float chanceOfProcessing) {
        super(modid, name, isDominant, isCombinable, baseTicksBetweenProcessing, chanceOfProcessing);
    }

    @Override
    public void performEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing, Random rand, World world, BlockPos pos, IBeeModifier housingModifier, IBeeModifier modeModifier, IEffectSettingsHolder settingsHolder) {
        AxisAlignedBB bounds = getAABB(genome, housing);

        mainLoop:
        for (int i = 0; i < 40; i++) {
            for (int y = Math.max(0, MathHelper.floor(bounds.minY)); y <= Math.min(255, MathHelper.ceil(bounds.maxY)); y++) {
                int x = getRand(MathHelper.floor(bounds.minX), MathHelper.ceil(bounds.maxX), rand);
                int z = getRand(MathHelper.floor(bounds.minZ), MathHelper.ceil(bounds.maxZ), rand);
                BlockPos pos1 = new BlockPos(x, y, z);
                IBlockState state = world.getBlockState(pos1);
                if (performPosEffect(world, pos1, state, genome, housing)) break mainLoop;
            }
        }
    }

    protected abstract boolean performPosEffect(World world, BlockPos pos, IBlockState state, IBeeGenome genome, IBeeHousing housing);

    @Override
    public boolean handleBlock(World world, BlockPos pos, EnumFacing facing, IBeeGenome genome, IBeeHousing housing) {
        return performPosEffect(world, pos, world.getBlockState(pos), genome, housing);
    }

    public static void sendParticle(World world, BlockPos pos, float r, float g, float b) {
        DoParticlePacket packet = new DoParticlePacket(r, g, b, pos);
        world.getPlayers(EntityPlayerMP.class, p -> true).forEach(p -> Messages.INSTANCE.sendTo(packet, p));
    }

    @SideOnly(Side.CLIENT)
    public static void doParticle(World world, BlockPos pos, float r, float g, float b) {
        double fxX = pos.getX() + 0.5F;
        double fxY = pos.getY() + 0.25F;
        double fxZ = pos.getZ() + 0.5F;
        float distanceFromCenter = 0.6F;
        float leftRightSpreadFromCenter = distanceFromCenter * (world.rand.nextFloat() - 0.5F);
        float upSpread = world.rand.nextFloat() * 6F / 16F;
        fxY += upSpread;

        ParticleHelper.spawnColoredDustParticle(world, fxX - distanceFromCenter, fxY, fxZ + leftRightSpreadFromCenter, r, g, b);
        ParticleHelper.spawnColoredDustParticle(world, fxX + distanceFromCenter, fxY, fxZ + leftRightSpreadFromCenter, r, g, b);
        ParticleHelper.spawnColoredDustParticle(world, fxX + leftRightSpreadFromCenter, fxY, fxZ - distanceFromCenter, r, g, b);
        ParticleHelper.spawnColoredDustParticle(world, fxX + leftRightSpreadFromCenter, fxY, fxZ + distanceFromCenter, r, g, b);
    }
}

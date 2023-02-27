package com.turing.customizablebees.util;

import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IEffectData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ParticleHelper {
    public static final BeeHiveFX BEE_HIVE_FX;

    @SideOnly(Side.CLIENT)
    public static boolean shouldSpawnParticle(World world) {
        Minecraft minecraft = Minecraft.getMinecraft();
        int setting = minecraft.gameSettings.particleSetting;

        if (setting == 2) return world.rand.nextInt(10) == 0;
        else if (setting == 1) return world.rand.nextInt(3) != 0;

        return true;
    }

    @SideOnly(Side.CLIENT)
    public static void spawnColoredDustParticle(World world, double x, double y, double z, float r, float g, float b) {
        if (!shouldSpawnParticle(world)) return;

        ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;
        effectRenderer.addEffect(new ParticleColored(world, x, y, z, r, g, b, 0, 0, 0));
    }

    static {
        BeeHiveFX temp;
        try {
            temp = forestry.core.render.ParticleRender::addBeeHiveFX;
        } catch (Throwable err) {
            err.printStackTrace();
            temp = (housing, genome, flowerPositions) -> {
                IAlleleBeeEffect effect = (IAlleleBeeEffect) AlleleManager.alleleRegistry.getAllele("forestry.effect.none");
                effect.doFX(genome, new IEffectData() {
                    @Override
                    public void setInteger(int index, int val) {

                    }

                    @Override
                    public void setBoolean(int index, boolean val) {

                    }

                    @Override
                    public int getInteger(int index) {
                        return 0;
                    }

                    @Override
                    public boolean getBoolean(int index) {
                        return false;
                    }

                    @Override
                    public void readFromNBT(NBTTagCompound nbt) {

                    }

                    @Override
                    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
                        return nbt;
                    }
                }, housing);
            };
        }
        BEE_HIVE_FX = temp;
    }

    public interface BeeHiveFX {
        void addBeeHiveFX(IBeeHousing housing, IBeeGenome genome, List<BlockPos> flowerPositions);
    }
}

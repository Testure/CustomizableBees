package com.turing.customizablebees.util;

import net.minecraft.client.particle.ParticleRedstone;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleColored extends ParticleRedstone {
    public ParticleColored(World world, double x, double y, double z, float red, float green, float blue, float f1, float f2, float f3) {
        super(world, x, y, z, 1.0F, f1, f2, f3);
        particleRed = red;
        particleGreen = green;
        particleBlue = blue;
    }
}

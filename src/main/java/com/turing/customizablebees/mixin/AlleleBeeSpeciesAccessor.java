package com.turing.customizablebees.mixin;

import forestry.apiculture.genetics.alleles.AlleleBeeSpecies;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = AlleleBeeSpecies.class, remap = false)
public interface AlleleBeeSpeciesAccessor {
    @Accessor
    Map<ItemStack, Float> getProductChances();

    @Accessor
    Map<ItemStack, Float> getSpecialtyChances();
}

package com.turing.customizablebees.api.groovyscript.recipe;

import forestry.apiculture.genetics.alleles.AlleleBeeSpecies;
import net.minecraft.item.ItemStack;

public class BeeProduct {
    public final ItemStack stack;
    public final float chance;
    public final boolean special;
    public final AlleleBeeSpecies species;

    public BeeProduct(AlleleBeeSpecies species, ItemStack stack, float chance, boolean special) {
        this.species = species;
        this.stack = stack;
        this.chance = chance;
        this.special = special;
    }
}

package com.turing.customizablebees.api;

import com.turing.customizablebees.bees.BeeMutationTree;
import com.turing.customizablebees.bees.CustomBeeEntry;
import com.turing.customizablebees.bees.CustomBees;
import com.turing.customizablebees.bees.effects.EffectBase;
import com.turing.customizablebees.items.CombItem;
import forestry.api.apiculture.IBeeMutationBuilder;
import forestry.api.genetics.IMutationBuilder;
import forestry.core.config.Constants;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class APIHelper {
    public static final List<CombItem> COMBS = new ArrayList<>();

    public static void addProduct(BeeMutationTree.SpeciesEntry species, ItemStack stack, float chance) {
        CustomBeeEntry bee = getCustomBeeSpecies(species.getEntryName());
        if (bee != null) {
            bee.addProduct(stack, chance);
        }
    }

    public static void addSpecialty(BeeMutationTree.SpeciesEntry species, ItemStack stack, float chance) {
        CustomBeeEntry bee = getCustomBeeSpecies(species.getEntryName());
        if (bee != null) {
            bee.addSpecialty(stack, chance);
        }
    }

    public static void addMutation(BeeMutationTree.SpeciesEntry a, BeeMutationTree.SpeciesEntry b, BeeMutationTree.SpeciesEntry result, double chance, @Nullable Function<IBeeMutationBuilder, IMutationBuilder> requirement) {
        CustomBees.addMutationRegister(tree -> tree.add(a, b, result, chance, requirement));
    }

    public static <T extends EffectBase> void addGroovyEffect(String name, Class<T> effect) {
        CustomBees.addGroovyEffect(name, effect);
    }

    public static void addEffect(EffectBase effect) {
        CustomBees.addEffect(effect);
    }

    public static CombItem addComb(CombItem comb) {
        COMBS.add(comb);
        return comb;
    }

    @Nullable
    public static CustomBeeEntry getCustomBeeSpecies(String name) {
        return CustomBeeEntry.BEE_ENTRIES.stream().filter(e -> e.getEntryName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static BeeMutationTree.SpeciesEntry getBeeSpecies(String name) {
        return new BeeMutationTree.GenericEntry(name);
    }

    public static BeeMutationTree.SpeciesEntry getForestryBeeSpecies(String name) {
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        return getBeeSpecies(Constants.MOD_ID + ".species" + name);
    }
}

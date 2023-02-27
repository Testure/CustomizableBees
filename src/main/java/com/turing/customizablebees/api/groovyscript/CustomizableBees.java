package com.turing.customizablebees.api.groovyscript;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.turing.customizablebees.api.*;
import com.turing.customizablebees.bees.BeeMutationTree;
import com.turing.customizablebees.bees.CustomBeeEntry;
import com.turing.customizablebees.bees.effects.EffectBase;
import com.turing.customizablebees.items.CombItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.Function;

public class CustomizableBees extends ModPropertyContainer {
    public void createBeeMutation(String a, String b, String result, double chance) {
        createBeeMutation(getBeeSpecies(a), getBeeSpecies(b), getBeeSpecies(result), chance);
    }

    public void createBeeMutation(String a, String b, String result, double chance, Function<MutationConditionBuilder, MutationConditionBuilder> requirement) {
        createBeeMutation(getBeeSpecies(a), getBeeSpecies(b), getBeeSpecies(result), chance, requirement);
    }

    public void createBeeMutation(BeeMutationTree.SpeciesEntry a, BeeMutationTree.SpeciesEntry b, BeeMutationTree.SpeciesEntry result, double chance, Function<MutationConditionBuilder, MutationConditionBuilder> requirement) {
        APIHelper.addMutation(a, b, result, chance, v -> requirement.apply(MutationConditionBuilder.wrap(v)).build());
    }

    public void createBeeMutation(BeeMutationTree.SpeciesEntry a, BeeMutationTree.SpeciesEntry b, BeeMutationTree.SpeciesEntry result, double chance) {
        APIHelper.addMutation(a, b, result, chance, null);
    }

    public void addProduct(String species, ItemStack stack, float chance) {
        addProduct(getBeeSpecies(species), stack, chance);
    }

    public void addProduct(BeeMutationTree.SpeciesEntry bee, ItemStack stack, float chance) {
        if (IngredientHelper.isEmpty(stack)) {
            GroovyLog.msg("Error adding bee product {}", stack)
                    .add("Items must not be empty")
                    .error()
                    .post();
            return;
        }
        APIHelper.addProduct(bee, stack, chance);
    }

    public void addSpecialty(String species, ItemStack stack, float chance) {
        addSpecialty(getBeeSpecies(species), stack, chance);
    }

    public void addSpecialty(BeeMutationTree.SpeciesEntry bee, ItemStack stack, float chance) {
        if (IngredientHelper.isEmpty(stack)) {
            GroovyLog.msg("Error adding bee specialty {}", stack)
                    .add("Items must not be empty")
                    .error()
                    .post();
            return;
        }
        APIHelper.addSpecialty(bee, stack, chance);
    }

    public <T extends EffectBase> void addEffect(String name, Class<T> effect) {
        APIHelper.addGroovyEffect(name, effect);
    }

    public BeeBuilder beeBuilder(String name, String branchName) {
        if (CustomBeeEntry.BEE_ENTRIES.stream().filter(e -> e.modelName.equalsIgnoreCase(name)).findAny().orElse(null) != null) {
            GroovyLog.msg("Bee with name '" + name + "' already exists!")
                    .error()
                    .post();
            return null;
        }
        return BeeBuilder.create("groovyscript", name, branchName);
    }

    public CombItem createCombItem(@Nullable CreativeTabs tab) {
        return new CombItem("groovyscript", tab);
    }

    public CombItem createAndRegisterCombItem(@Nullable CreativeTabs tab, ICombType... types) {
        CombItem comb = createCombItem(tab);
        for (ICombType type : types) comb.addType(type);
        return addComb(comb);
    }

    public CombItem addComb(CombItem comb) {
        return APIHelper.addComb(comb);
    }

    public CombRecipeBuilder combRecipeBuilder(ICombType type) {
        return new CombRecipeBuilder(type);
    }

    public CombTypeBuilder combTypeBuilder(String name) {
        return CombTypeBuilder.start(name);
    }

    public BeeMutationTree.SpeciesEntry getBeeSpecies(String name) {
        return APIHelper.getBeeSpecies(name);
    }

    public BeeMutationTree.SpeciesEntry getForestryBeeSpecies(String name) {
        return APIHelper.getForestryBeeSpecies(name);
    }
}

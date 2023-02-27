package com.turing.customizablebees.api.crafttweaker;

import com.turing.customizablebees.api.APIHelper;
import com.turing.customizablebees.api.CombTypeBuilder;
import com.turing.customizablebees.items.CombItem;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.creativetab.CreativeTabs;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.function.Function;

@ZenRegister
@ZenClass("mods.customizablebees.CustomizableBees")
public class CrafttweakerAPI {
    @ZenMethod
    public static void createBeeMutation(String a, String b, String result, double chance) {
        APIHelper.addMutation(APIHelper.getBeeSpecies(a), APIHelper.getBeeSpecies(b), APIHelper.getBeeSpecies(result), chance, null);
    }

    @ZenMethod
    public static void createBeeMutation(String a, String b, String result, double chance, Function<ConditionBuilderWrapper, ConditionBuilderWrapper> requirement) {
        APIHelper.addMutation(APIHelper.getBeeSpecies(a), APIHelper.getBeeSpecies(b), APIHelper.getBeeSpecies(result), chance, v -> requirement.apply(ConditionBuilderWrapper.wrap(v)).build());
    }

    @ZenMethod
    public static void addProduct(String species, IItemStack stack, float chance) {
        APIHelper.addProduct(APIHelper.getBeeSpecies(species), CraftTweakerMC.getItemStack(stack), chance);
    }

    @ZenMethod
    public static void addSpecialty(String species, IItemStack stack, float chance) {
        APIHelper.addSpecialty(APIHelper.getBeeSpecies(species), CraftTweakerMC.getItemStack(stack), chance);
    }

    @ZenMethod
    public static BeeBuilderWrapper beeBuilder(String name, String branchName) {
        return BeeBuilderWrapper.create(name, branchName);
    }

    @ZenMethod
    public static void createCombWithTypes(ICombTypeCT... types) {
        CombItem comb = new CombItem("crafttweaker", CreativeTabs.MATERIALS);
        for (ICombTypeCT type : types) comb.addType(type);
        APIHelper.addComb(comb);
    }

    @ZenMethod
    public static ICombTypeCT createCombType(String name, boolean shouldShow, int primaryColor, int secondaryColor) {
        CombTypeBuilder builder = CombTypeBuilder.start(name).setPrimaryColor(primaryColor).setSecondaryColor(secondaryColor);
        if (!shouldShow) builder = builder.setIsSecret();
        return (ICombTypeCT) builder.build();
    }
}

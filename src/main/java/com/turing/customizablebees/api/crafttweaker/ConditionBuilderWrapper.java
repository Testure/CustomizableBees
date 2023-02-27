package com.turing.customizablebees.api.crafttweaker;

import com.turing.customizablebees.api.MutationConditionBuilder;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBiomeType;
import forestry.api.apiculture.IBeeMutationBuilder;
import forestry.api.genetics.IMutationBuilder;
import net.minecraftforge.common.BiomeDictionary;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Arrays;

@ZenRegister
@ZenClass("mods.customizablebees.MutationConditionBuilder")
public class ConditionBuilderWrapper extends MutationConditionBuilder {
    protected ConditionBuilderWrapper(IBeeMutationBuilder builder) {
        super(builder);
    }

    public static ConditionBuilderWrapper wrap(IBeeMutationBuilder builder) {
        return new ConditionBuilderWrapper(builder);
    }

    public IMutationBuilder build() {
        return this.builder;
    }

    @ZenMethod
    public ConditionBuilderWrapper requireDay() {
        super.requireDay();
        return this;
    }

    @ZenMethod
    public ConditionBuilderWrapper requireNight() {
        super.requireNight();
        return this;
    }

    @ZenMethod
    public ConditionBuilderWrapper requireTemperature(int temp) {
        super.requireTemperature(temp);
        return this;
    }

    @ZenMethod
    public ConditionBuilderWrapper requireTemperature(int minTemp, int maxTemp) {
        super.requireTemperature(minTemp, maxTemp);
        return this;
    }

    @ZenMethod
    public ConditionBuilderWrapper requireHumidity(int humidity) {
        super.requireHumidity(humidity);
        return this;
    }

    @ZenMethod
    public ConditionBuilderWrapper requireHumidity(int minHumidity, int maxHumidity) {
        super.requireHumidity(minHumidity, maxHumidity);
        return this;
    }

    @ZenMethod
    public ConditionBuilderWrapper requireDataRange(int startMonth, int startDay, int endMonth, int endDay) {
        super.requireDataRange(startMonth, startDay, endMonth, endDay);
        return this;
    }

    @ZenMethod
    public ConditionBuilderWrapper requireFoundationOreDictionary(String oreDictName) {
        super.requireFoundationOreDictionary(oreDictName);
        return this;
    }

    @ZenMethod
    public ConditionBuilderWrapper requireFoundationBlock(IBlockState state) {
        super.requireFoundationBlock(CraftTweakerMC.getBlockState(state));
        return this;
    }

    @ZenMethod
    public ConditionBuilderWrapper requireFoundationStates(IBlockState... validStates) {
        net.minecraft.block.state.IBlockState[] states = Arrays.stream(validStates).map(CraftTweakerMC::getBlockState).toArray(net.minecraft.block.state.IBlockState[]::new);
        super.requireFoundationStates(states);
        return this;
    }

    @ZenMethod
    public ConditionBuilderWrapper requireBiome(IBiomeType biomeType) {
        super.requireBiome(CraftTweakerMC.getBiomeType(biomeType));
        return this;
    }

    @ZenMethod
    public ConditionBuilderWrapper requireBiomes(IBiomeType... validBiomes) {
        BiomeDictionary.Type[] biomeTypes = Arrays.stream(validBiomes).map(CraftTweakerMC::getBiomeType).toArray(BiomeDictionary.Type[]::new);
        super.requireBiomes(biomeTypes);
        return this;
    }
}

package com.turing.customizablebees.api;

import forestry.api.apiculture.IBeeMutationBuilder;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.IMutationBuilder;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.BiomeDictionary;

public class MutationConditionBuilder {
    protected IMutationBuilder builder;

    protected MutationConditionBuilder(IBeeMutationBuilder builder) {
        this.builder = builder;
    }

    public static MutationConditionBuilder wrap(IBeeMutationBuilder builder) {
        return new MutationConditionBuilder(builder);
    }

    public IMutationBuilder build() {
        return builder;
    }

    public MutationConditionBuilder requireDay() {
        this.builder = builder.requireDay();
        return this;
    }

    public MutationConditionBuilder requireNight() {
        this.builder = builder.requireNight();
        return this;
    }

    public MutationConditionBuilder requireTemperature(int temp) {
        temp = Math.max(Math.min(temp, 6), 0);
        this.builder = builder.restrictTemperature(EnumTemperature.values()[temp]);
        return this;
    }

    public MutationConditionBuilder requireTemperature(int minTemp, int maxTemp) {
        minTemp = Math.max(Math.min(minTemp, 6), 0);
        maxTemp = Math.max(Math.min(maxTemp, 6), 0);
        this.builder = builder.restrictTemperature(EnumTemperature.values()[minTemp], EnumTemperature.values()[maxTemp]);
        return this;
    }

    public MutationConditionBuilder requireHumidity(int humidity) {
        humidity = Math.max(Math.min(humidity, 6), 0);
        this.builder = builder.restrictHumidity(EnumHumidity.values()[humidity]);
        return this;
    }

    public MutationConditionBuilder requireHumidity(int minHumidity, int maxHumidity) {
        minHumidity = Math.max(Math.min(minHumidity, 6), 0);
        maxHumidity = Math.max(Math.min(maxHumidity, 6), 0);
        this.builder = builder.restrictHumidity(EnumHumidity.values()[minHumidity], EnumHumidity.values()[maxHumidity]);
        return this;
    }

    public MutationConditionBuilder requireDataRange(int startMonth, int startDay, int endMonth, int endDay) {
        this.builder = builder.restrictDateRange(startMonth, startDay, endMonth, endDay);
        return this;
    }

    public MutationConditionBuilder requireFoundationOreDictionary(String oreDictName) {
        this.builder = builder.requireResource(oreDictName);
        return this;
    }

    public MutationConditionBuilder requireFoundationBlock(IBlockState state) {
        this.builder = builder.requireResource(state);
        return this;
    }

    public MutationConditionBuilder requireFoundationStates(IBlockState... validStates) {
        this.builder = builder.requireResource(validStates);
        return this;
    }

    public MutationConditionBuilder requireBiome(BiomeDictionary.Type biome) {
        this.builder = builder.restrictBiomeType(biome);
        return this;
    }

    public MutationConditionBuilder requireBiomes(BiomeDictionary.Type... validBiomes) {
        this.builder = builder.restrictBiomeType(validBiomes);
        return this;
    }
}

package com.turing.customizablebees.bees.effects;

import com.turing.customizablebees.bees.effects.settings.IEffectSettingsHolder;
import forestry.api.apiculture.*;
import forestry.api.genetics.IEffectData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public abstract class ThrottledEffectBase extends EffectBase {
    public final float baseTicksBetweenProcessing;
    public final float chanceOfProcessing;

    public ThrottledEffectBase(String modid, String name, float baseTicksBetweenProcessing) {
        this(modid, name, baseTicksBetweenProcessing, 1);
    }

    public ThrottledEffectBase(String modid, String name, float baseTicksBetweenProcessing, float chanceOfProcessing) {
        this(modid, name, false, false, baseTicksBetweenProcessing, chanceOfProcessing);
    }

    public ThrottledEffectBase(String modid, String name, boolean isDominant, boolean isCombinable, float baseTicksBetweenProcessing, float chanceOfProcessing) {
        super(modid, name, isDominant, isCombinable);
        this.baseTicksBetweenProcessing = baseTicksBetweenProcessing;
        this.chanceOfProcessing = chanceOfProcessing;
    }

    @Override
    public IEffectData validateStorage(IEffectData storedData) {
        if (storedData instanceof BaseEffectDataMap.IntMap) return storedData;
        return new BaseEffectDataMap.IntMap();
    }

    @Override
    public IEffectData doEffectBase(IBeeGenome genome, IEffectData storedData, IBeeHousing housing, IEffectSettingsHolder settingsHolder) {
        int time = storedData.getInteger(0);
        time++;
        storedData.setInteger(0, time);

        World world = housing.getWorldObj();
        IBeekeepingMode mode = BeeManager.beeRoot.getBeekeepingMode(world);
        IBeeModifier housingModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);
        IBeeModifier modeModifier = mode.getBeeModifier();
        float speed = getSpeed(genome, housing);

        if (time * speed < baseTicksBetweenProcessing) return storedData;

        storedData.setInteger(0, 0);

        if (chanceOfProcessing < 1 && world.rand.nextFloat() > chanceOfProcessing) return storedData;

        performEffect(genome, storedData, housing, world.rand, world, housing.getCoordinates(), housingModifier, modeModifier, settingsHolder);

        return storedData;
    }

    public abstract void performEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing, Random rand, World world, BlockPos pos, IBeeModifier housingModifier, IBeeModifier modeModifier, IEffectSettingsHolder settingsHolder);

    @Override
    public float getCooldown(IBeeGenome genome, Random rand) {
        float speed = genome.getSpeed();
        float base = baseTicksBetweenProcessing / speed;
        float result = base;
        if (chanceOfProcessing < 1) while (rand.nextFloat() > chanceOfProcessing) result += base;
        return result;
    }
}

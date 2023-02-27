package com.turing.customizablebees.api.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBiome;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import crafttweaker.mc1120.item.MCItemStack;
import crafttweaker.mc1120.world.MCBiome;
import crafttweaker.mc1120.world.MCBlockPos;
import crafttweaker.mc1120.world.MCWorld;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.customizablebees.JubilanceHelper")
public class JubilanceHelper {
    private final IAlleleBeeSpecies species;
    private final IBeeGenome genome;
    private final IBeeHousing housing;

    private JubilanceHelper(IAlleleBeeSpecies species, IBeeGenome genome, IBeeHousing housing) {
        this.species = species;
        this.genome = genome;
        this.housing = housing;
    }

    public static JubilanceHelper create(IAlleleBeeSpecies species, IBeeGenome genome, IBeeHousing housing) {
        return new JubilanceHelper(species, genome, housing);
    }

    @ZenMethod
    public int getLightLevel() {
        return housing.getBlockLightValue();
    }

    @ZenMethod
    public boolean canSeeSky() {
        return housing.canBlockSeeTheSky();
    }

    @ZenMethod
    public boolean isRaining() {
        return housing.isRaining();
    }

    @ZenMethod
    public IItemStack getQueen() {
        return MCItemStack.createNonCopy(housing.getBeeInventory().getQueen());
    }

    @ZenMethod
    public IItemStack getDrone() {
        return MCItemStack.createNonCopy(housing.getBeeInventory().getDrone());
    }

    @ZenMethod
    public void setQueen(IItemStack newStack) {
        housing.getBeeInventory().setQueen(CraftTweakerMC.getItemStack(newStack));
    }

    @ZenMethod
    public void setDrone(IItemStack newStack){
        housing.getBeeInventory().setDrone(CraftTweakerMC.getItemStack(newStack));
    }

    @ZenMethod
    public boolean canWork() {
        return housing.getBeekeepingLogic().canWork();
    }

    @ZenMethod
    public int getProgress() {
        return housing.getBeekeepingLogic().getBeeProgressPercent();
    }

    @ZenMethod
    public IBlockPos getPosition() {
        return new MCBlockPos(housing.getCoordinates());
    }

    @ZenMethod
    public IBlockPos[] getFlowerPositions() {
        return housing.getBeekeepingLogic().getFlowerPositions().stream().map(MCBlockPos::new).toArray(IBlockPos[]::new);
    }

    @ZenMethod
    public IBiome getBiome() {
        return new MCBiome(housing.getBiome());
    }

    @ZenMethod
    public int getHumidity() {
        return housing.getHumidity().ordinal();
    }

    @ZenMethod
    public int getTemperature() {
        return housing.getTemperature().ordinal();
    }

    @ZenMethod
    public IWorld getWorld() {
        return new MCWorld(housing.getWorldObj());
    }

    @ZenRegister
    @ZenClass("mods.customizablebees.JubilanceProvider")
    public interface JubilanceProvider {
        boolean handle(JubilanceHelper helper);
    }
}

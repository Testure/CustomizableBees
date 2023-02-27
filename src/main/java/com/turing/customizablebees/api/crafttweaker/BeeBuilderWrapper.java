package com.turing.customizablebees.api.crafttweaker;

import com.turing.customizablebees.bees.CustomBeeEntry;
import com.turing.customizablebees.bees.CustomBeeModel;
import com.turing.customizablebees.bees.CustomBees;
import crafttweaker.annotations.ZenRegister;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import net.minecraftforge.fml.common.Loader;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;

@ZenRegister
@ZenClass("mods.customizablebees.BeeBuilder")
public class BeeBuilderWrapper {
    protected int primaryColor = col(0, 0, 0);
    protected int secondaryColor = 0xffdc16;
    protected boolean dominant = false;
    protected boolean shouldAddVanillaProducts = true;
    protected boolean isNocturnal = false;
    protected boolean toleratesRain = false;
    protected final String name;
    protected final String branchName;
    protected final String modid;
    protected String authority = "Unknown";
    protected String effect = "";
    protected String customModel = "";
    protected EnumTemperature temperature = EnumTemperature.NORMAL;
    protected EnumHumidity humidity = EnumHumidity.NORMAL;
    @ZenProperty
    public JubilanceHelper.JubilanceProvider jubilanceProvider = null;

    protected BeeBuilderWrapper(String name, String branchName) {
        this.modid = Loader.isModLoaded("contenttweaker") ? "contenttweaker" : "crafttweaker";
        this.name = name;
        this.branchName = branchName;
    }

    public static BeeBuilderWrapper create(String name, String branchName) {
        return new BeeBuilderWrapper(name, branchName);
    }

    @ZenMethod
    public BeeBuilderWrapper setNocturnal() {
        this.isNocturnal = true;
        return this;
    }

    @ZenMethod
    public BeeBuilderWrapper setDominant() {
        this.dominant = true;
        return this;
    }

    @ZenMethod
    public BeeBuilderWrapper setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
        return this;
    }

    @ZenMethod
    public BeeBuilderWrapper setPrimaryColor(int r, int g, int b) {
        this.primaryColor = col(r, g, b);
        return this;
    }

    @ZenMethod
    public BeeBuilderWrapper setSecondaryColor(int secondaryColor) {
        this.secondaryColor = secondaryColor;
        return this;
    }

    @ZenMethod
    public BeeBuilderWrapper setSecondaryColor(int r, int g, int b) {
        this.secondaryColor = col(r, g, b);
        return this;
    }

    @ZenMethod
    public BeeBuilderWrapper setTemperature(int temperature) {
        temperature = Math.max(Math.min(temperature, 6), 0);
        this.temperature = EnumTemperature.values()[temperature];
        return this;
    }

    @ZenMethod
    public BeeBuilderWrapper setHumidity(int humidity) {
        humidity = Math.max(Math.min(humidity, 2), 0);
        this.humidity = EnumHumidity.values()[humidity];
        return this;
    }

    @ZenMethod
    public BeeBuilderWrapper setCustomModel(String suffix) {
        this.customModel = suffix;
        return this;
    }

    @ZenMethod
    public BeeBuilderWrapper toleratesRain() {
        this.toleratesRain = true;
        return this;
    }

    @ZenMethod
    public BeeBuilderWrapper removeVanillaProducts() {
        this.shouldAddVanillaProducts = false;
        return this;
    }

    @ZenMethod
    public BeeBuilderWrapper setEffect(String effectName) {
        this.effect = effectName;
        return this;
    }

    @ZenMethod
    public BeeBuilderWrapper setAuthority(String authority) {
        this.authority = authority;
        return this;
    }

    @ZenMethod
    public String build() {
        CustomBeeEntry bee = new CustomBeeEntry(this.modid, this.name, this.dominant, this.branchName, this.primaryColor, this.secondaryColor)
                .setAuthority(this.authority)
                .setTemperature(this.temperature)
                .setHumidity(this.humidity);
        if (!shouldAddVanillaProducts) bee = bee.removeVanillaProducts();
        if (this.isNocturnal) bee = bee.setNocturnal();
        if (this.toleratesRain) bee = bee.setTemplateAlleleBool(EnumBeeChromosome.TOLERATES_RAIN, true);
        if (this.jubilanceProvider != null) bee = bee.setJubilanceProvider((a, b, c) -> jubilanceProvider.handle(JubilanceHelper.create(a, b, c)));
        if (!this.customModel.isEmpty()) bee = bee.setCustomBeeModelProvider(new CustomBeeModel("contenttweaker", this.customModel));
        if (!this.effect.isEmpty()) bee = bee.setTemplateEffect(() -> CustomBees.getEffects().get(this.effect));
        return modid + "." + name;
    }

    private static int col(int r, int g, int b) {
        return ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
    }
}

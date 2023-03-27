package com.turing.customizablebees.api;

import com.cleanroommc.groovyscript.GroovyScript;
import com.turing.customizablebees.bees.BeeMutationTree;
import com.turing.customizablebees.bees.CustomBeeEntry;
import com.turing.customizablebees.bees.CustomBeeModel;
import com.turing.customizablebees.bees.CustomBees;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IJubilanceProvider;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;

public class BeeBuilder {
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
    protected IJubilanceProvider jubilanceProvider;

    protected BeeBuilder(String modid, String name, String branchName) {
        this.name = name;
        this.branchName = branchName;
        this.modid = modid;
    }

    public static BeeBuilder create(String modid, String name, String branchName) {
        return new BeeBuilder(modid, name, branchName);
    }

    public BeeBuilder setDominant() {
        this.dominant = true;
        return this;
    }

    public BeeBuilder removeVanillaProducts() {
        this.shouldAddVanillaProducts = false;
        return this;
    }

    public BeeBuilder setNocturnal() {
        this.isNocturnal = true;
        return this;
    }

    public BeeBuilder toleratesRain() {
        this.toleratesRain = true;
        return this;
    }

    public BeeBuilder setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
        return this;
    }

    public BeeBuilder setPrimaryColor(int r, int g, int b) {
        this.primaryColor = col(r, g, b);
        return this;
    }

    public BeeBuilder setSecondaryColor(int secondaryColor) {
        this.secondaryColor = secondaryColor;
        return this;
    }

    public BeeBuilder setSecondaryColor(int r, int g, int b) {
        this.secondaryColor = col(r, g, b);
        return this;
    }

    public BeeBuilder setAuthority(String authority) {
        this.authority = authority;
        return this;
    }

    public BeeBuilder setEffect(String effectName) {
        this.effect = effectName;
        return this;
    }

    public BeeBuilder setCustomModel(String suffix) {
        this.customModel = suffix;
        return this;
    }

    public BeeBuilder setTemperature(EnumTemperature temperature) {
        this.temperature = temperature;
        return this;
    }

    public BeeBuilder setHumidity(EnumHumidity humidity) {
        this.humidity = humidity;
        return this;
    }

    public BeeBuilder setJubilanceProvider(IJubilanceProvider jubilanceProvider) {
        this.jubilanceProvider = jubilanceProvider;
        return this;
    }

    public BeeMutationTree.SpeciesEntry build() {
        CustomBeeEntry bee = new CustomBeeEntry(this.modid, this.name, this.dominant, this.branchName, this.primaryColor, this.secondaryColor)
                .setAuthority(this.authority)
                .setTemperature(this.temperature)
                .setHumidity(this.humidity);
        if (!shouldAddVanillaProducts) bee = bee.removeVanillaProducts();
        if (this.isNocturnal) bee = bee.setNocturnal();
        if (this.toleratesRain) bee = bee.setTemplateAlleleBool(EnumBeeChromosome.TOLERATES_RAIN, true);
        if (this.jubilanceProvider != null) bee = bee.setJubilanceProvider(jubilanceProvider);
        if (!this.customModel.isEmpty()) bee = bee.setCustomBeeModelProvider(new CustomBeeModel(!this.modid.equalsIgnoreCase("groovyscript") ? this.modid : GroovyScript.getRunConfig().getPackId(), this.customModel));
        if (!this.effect.isEmpty()) bee = bee.setTemplateEffect(() -> CustomBees.getEffects().get(this.effect));
        return bee;
    }

    private static int col(int r, int g, int b) {
        return ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
    }
}

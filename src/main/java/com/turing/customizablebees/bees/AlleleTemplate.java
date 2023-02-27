package com.turing.customizablebees.bees;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.core.config.Constants;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Objects;

public class AlleleTemplate {
    final EnumMap<EnumBeeChromosome, IAllele> map = new EnumMap<>(EnumBeeChromosome.class);

    private AlleleTemplate(@Nullable IAlleleBeeSpecies species) {
        if (species != null) setTemplateAllele(EnumBeeChromosome.SPECIES, species);

        setTemplateAllele(EnumBeeChromosome.SPEED, "forestry.speedSlow");
        setTemplateAllele(EnumBeeChromosome.LIFESPAN, "forestry.lifespanLong");
        setTemplateAllele(EnumBeeChromosome.FERTILITY, "forestry.fertilityNormal");
        setTemplateAllele(EnumBeeChromosome.TEMPERATURE_TOLERANCE, "forestry.toleranceNone");
        setTemplateAllele(EnumBeeChromosome.NEVER_SLEEPS, "forestry.boolFalse");
        setTemplateAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE, "forestry.toleranceNone");
        setTemplateAllele(EnumBeeChromosome.TOLERATES_RAIN, "forestry.boolFalse");
        setTemplateAllele(EnumBeeChromosome.CAVE_DWELLING, "forestry.boolFalse");
        setTemplateAllele(EnumBeeChromosome.FLOWER_PROVIDER, "forestry.flowersVanilla");
        setTemplateAllele(EnumBeeChromosome.FLOWERING, "forestry.floweringSlow");
        setTemplateAllele(EnumBeeChromosome.TERRITORY, "forestry.territoryAverage");
        setTemplateAllele(EnumBeeChromosome.EFFECT, "forestry.effectNone");
    }

    public static AlleleTemplate createAlleleTemplate(IAlleleBeeSpecies species) {
        return new AlleleTemplate(species);
    }

    public IAlleleBeeEffect getEffect() {
        return getValue(EnumBeeChromosome.EFFECT, IAlleleBeeEffect.class);
    }

    public <T extends IAllele> T getValue(EnumBeeChromosome chromosome, Class<T> clazz) {
        Validate.isTrue(chromosome.getAlleleClass().isAssignableFrom(clazz));
        IAllele allele = map.get(chromosome);
        return (T) allele;
    }

    public void register() {
        IAllele[] alleles = new IAllele[EnumBeeChromosome.values().length];
        for (int i = 0; i < alleles.length; i++) {
            EnumBeeChromosome key = EnumBeeChromosome.values()[i];
            if (!map.containsKey(key)) throw new IllegalStateException(key + " entry not found");
            alleles[i] = map.get(key);
        }
        Objects.requireNonNull(BeeManager.beeRoot).registerTemplate(alleles);
    }

    public AlleleTemplate setTemplateAllele(EnumBeeChromosome chromosome, Object value) {
        if (value instanceof IAllele) {
            map.put(chromosome, (IAllele) value);
            return this;
        } else if (value instanceof String) {
            for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles(chromosome)) {
                if (value.equals(allele.getUID())) {
                    map.put(chromosome, allele);
                    return this;
                }
            }

            for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles(chromosome)) {
                if (allele.getModID().equals(Constants.MOD_ID) && allele.getUID().contains((String) value)) {
                    map.put(chromosome, allele);
                    return this;
                }
            }

            String s = AlleleManager.alleleRegistry.getRegisteredAlleles(chromosome).stream()
                    .map(IAllele::getUID)
                    .collect(StringBuilder::new, (stringBuilder, str) -> stringBuilder.append(str).append(' '), StringBuilder::append).toString();
            throw new RuntimeException("Error[" + value + "] + " + chromosome + " {" + s + "}");
        } else throw new RuntimeException("Error[" + value + "] + " + chromosome);
    }
}

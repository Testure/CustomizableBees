package com.turing.customizablebees.util;

import forestry.api.apiculture.IAlleleBeeSpecies;
import net.bdew.gendustry.api.EnumMutationSetting;
import net.bdew.gendustry.api.GendustryAPI;

public class GendustryAPIWorker extends GendustryAPIHelper {
    @Override
    public void forceMutation(IAlleleBeeSpecies species) {
        GendustryAPI.Registries.getMutatronOverrides().set(species, EnumMutationSetting.DISABLED);
    }
}

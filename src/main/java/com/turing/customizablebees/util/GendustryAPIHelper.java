package com.turing.customizablebees.util;

import forestry.api.apiculture.IAlleleBeeSpecies;
import net.minecraftforge.fml.common.Loader;

public class GendustryAPIHelper {
    private static GendustryAPIHelper INSTANCE;

    public static GendustryAPIHelper getInstance() {
        if (INSTANCE == null) {
            if (Loader.isModLoaded("gendustry")) return new GendustryAPIWorker();
            else return new GendustryAPIHelper();
        }
        return INSTANCE;
    }

    public void forceMutation(IAlleleBeeSpecies species) {

    }
}

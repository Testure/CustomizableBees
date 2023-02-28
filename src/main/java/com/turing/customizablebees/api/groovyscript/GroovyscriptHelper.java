package com.turing.customizablebees.api.groovyscript;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.sandbox.LoadStage;

public class GroovyscriptHelper {
    public static ModSupport.Container<CustomizableBees> CUSTOMIZABLE_BEES;

    public static void construction() {
        CUSTOMIZABLE_BEES = new ModSupport.Container<>("customizablebees", "Customizable Bees", CustomizableBees::new, "custombees", "forestrytweaker", "forestry");
    }

    public static void init() {

    }
}

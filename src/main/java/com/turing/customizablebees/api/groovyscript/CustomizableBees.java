package com.turing.customizablebees.api.groovyscript;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class CustomizableBees extends ModPropertyContainer {
    public final Bees bees = new Bees();
    public final Combs combs = new Combs();

    public CustomizableBees() {
        addRegistry(combs);
    }
}

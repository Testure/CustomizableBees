package com.turing.customizablebees.api.groovyscript;

import com.cleanroommc.groovyscript.brackets.BracketHandlerManager;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class CustomizableBees extends ModPropertyContainer {
    public final Centrifuge centrifuge = new Centrifuge();
    public final Squeezer squeezer = new Squeezer();
    public final Carpenter carpenter = new Carpenter();
    public final Still still = new Still();
    public final Moistener moistener = new Moistener();
    public final CharcoalPile charcoalPile = new CharcoalPile();
    public final Fermenter fermenter = new Fermenter();
    public final ThermionicFabricator thermionicFabricator = new ThermionicFabricator();
    public final Bees bees = new Bees();
    public final Combs combs = new Combs();

    public CustomizableBees() {
        addRegistry(centrifuge);
        addRegistry(squeezer);
        addRegistry(carpenter);
        addRegistry(still);
        addRegistry(moistener);
        addRegistry(charcoalPile);
        addRegistry(fermenter);
        addRegistry(thermionicFabricator);
        addRegistry(thermionicFabricator.smelting);
        addRegistry(moistener.fuel);
        addRegistry(bees);
        addRegistry(bees.mutations);
        addRegistry(combs);
    }

    @Override
    public void initialize() {
        if (BracketHandlerManager.getBracketHandler("species") == null) BracketHandlerManager.registerBracketHandler("species", SpeciesBracketHandler.INSTANCE);
    }
}

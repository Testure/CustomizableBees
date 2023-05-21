package com.turing.customizablebees.api.groovyscript;

import com.cleanroommc.groovyscript.GroovyScript;
import com.turing.customizablebees.api.APIHelper;
import com.turing.customizablebees.api.BeeBuilder;
import com.turing.customizablebees.bees.effects.EffectBase;

public class Bees {
    public BeeBuilder beeBuilder(String name, String branchName) {
        return BeeBuilder.create(GroovyScript.ID, name, branchName);
    }

    public <T extends EffectBase> void addEffect(String effectName, Class<T> effect) {
        APIHelper.addGroovyEffect(effectName, effect);
    }
}

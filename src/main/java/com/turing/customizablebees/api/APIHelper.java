package com.turing.customizablebees.api;

import com.turing.customizablebees.bees.CustomBees;
import com.turing.customizablebees.bees.effects.EffectBase;
import com.turing.customizablebees.items.CombItem;

import java.util.ArrayList;
import java.util.List;

public class APIHelper {
    public static final List<CombItem> COMBS = new ArrayList<>();

    public static <T extends EffectBase> void addGroovyEffect(String name, Class<T> effect) {
        CustomBees.addGroovyEffect(name, effect);
    }

    public static void addEffect(EffectBase effect) {
        CustomBees.addEffect(effect);
    }

    public static CombItem addComb(CombItem comb) {
        COMBS.add(comb);
        return comb;
    }
}

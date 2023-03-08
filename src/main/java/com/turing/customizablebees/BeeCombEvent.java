package com.turing.customizablebees;

import com.google.common.collect.ImmutableSet;
import com.turing.customizablebees.items.CombItem;
import net.minecraftforge.fml.common.eventhandler.Event;

public class BeeCombEvent {
    public static class Define extends Event {

    }

    public static class OnRegister extends Event {
        public final ImmutableSet<CombItem> combs;

        public OnRegister() {
            this.combs = ImmutableSet.copyOf(CombItem.ALL_COMB_TYPES.keySet());
        }
    }

    public static class RegisterRecipes extends Event {

    }
}

package com.turing.customizablebees;

import com.google.common.collect.ImmutableList;
import com.turing.customizablebees.bees.CustomBeeEntry;
import com.turing.customizablebees.bees.CustomBees;
import com.turing.customizablebees.bees.effects.EffectBase;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;
import java.util.Map;

public class BeeDefinitionEvent {
    public static class Pre extends Event {

    }

    public static class Post extends Event {
        public final List<CustomBeeEntry> bees = ImmutableList.copyOf(CustomBeeEntry.BEE_ENTRIES);
        public final Map<String, EffectBase> effects = CustomBees.getEffects();
    }

    public static class DefineMutations extends Event {
        public final List<CustomBeeEntry> bees = ImmutableList.copyOf(CustomBeeEntry.BEE_ENTRIES);
    }

    public static class DefineEffects extends Event {

    }
}

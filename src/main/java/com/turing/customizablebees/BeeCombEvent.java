package com.turing.customizablebees;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.turing.customizablebees.api.ICombType;
import com.turing.customizablebees.items.CombItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.AbstractMap;
import java.util.Map;

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
        public final Map<CombItem, Map<ICombType, Map<ItemStack, Float>>> recipes;

        public RegisterRecipes() {
            ImmutableMap.Builder<CombItem, Map<ICombType, Map<ItemStack, Float>>> builder = ImmutableMap.builder();
            CombItem.ALL_COMB_TYPES.forEach((comb, a) -> builder.put(new AbstractMap.SimpleEntry<>(comb, comb.recipes)));
            recipes = builder.build();
        }
    }

    public static class DefineRecipes extends Event {

    }
}

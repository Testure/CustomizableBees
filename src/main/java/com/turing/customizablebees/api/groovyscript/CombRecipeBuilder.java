package com.turing.customizablebees.api.groovyscript;

import com.turing.customizablebees.api.ICombType;
import com.turing.customizablebees.items.CombItem;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CombRecipeBuilder {
    private final ICombType type;
    private final Map<ItemStack, Float> products = new HashMap<>();

    protected CombRecipeBuilder(ICombType type) {
        this.type = type;
    }

    public static CombRecipeBuilder start(ICombType type) {
        return new CombRecipeBuilder(type);
    }

    public CombRecipeBuilder addProduct(ItemStack stack) {
        return addProduct(stack, 1.0F);
    }

    public CombRecipeBuilder addProduct(ItemStack stack, float chance) {
        products.put(stack, chance);
        return this;
    }

    public void build(CombItem comb) {
        comb.addRecipe(type, products);
    }
}

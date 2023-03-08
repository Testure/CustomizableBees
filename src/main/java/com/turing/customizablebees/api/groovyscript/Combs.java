package com.turing.customizablebees.api.groovyscript;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.turing.customizablebees.api.APIHelper;
import com.turing.customizablebees.api.CombTypeBuilder;
import com.turing.customizablebees.api.ICombType;
import com.turing.customizablebees.items.CombItem;
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.factory.recipes.CentrifugeRecipe;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class Combs extends VirtualizedRegistry<ICentrifugeRecipe> {
    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(RecipeManagers.centrifugeManager::removeRecipe);
        restoreFromBackup().forEach(RecipeManagers.centrifugeManager::addRecipe);
    }

    public CombTypeBuilder typeBuilder(String name) {
        return CombTypeBuilder.start(name);
    }

    public CombRecipeBuilder recipeBuilder(ICombType type) {
        return new CombRecipeBuilder(type);
    }

    public void add(ICentrifugeRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        RecipeManagers.centrifugeManager.addRecipe(recipe);
    }

    public CombItem addComb(CombItem comb) {
        return APIHelper.addComb(comb);
    }

    public CombItem createCombItem(@Nullable CreativeTabs tab) {
        return new CombItem(GroovyScript.ID, tab);
    }

    public CombItem createAndRegisterCombItem(@Nullable CreativeTabs tab, ICombType... types) {
        CombItem comb = createCombItem(tab);
        for (ICombType type : types) comb.addType(type);
        return addComb(comb);
    }

    public CombItem getComb() {
        return APIHelper.COMBS.stream().filter(c -> c.modid.equals(GroovyScript.ID)).findAny().orElseThrow(NullPointerException::new);
    }

    public class CombRecipeBuilder {
        private final ICombType type;
        private final Map<ItemStack, Float> products = new HashMap<>();

        public CombRecipeBuilder(ICombType type) {
            this.type = type;
        }

        public CombRecipeBuilder addProduct(ItemStack stack) {
            return addProduct(stack, 1.0F);
        }

        public CombRecipeBuilder addProduct(ItemStack stack, float chance) {
            products.put(stack, chance);
            return this;
        }

        public ICentrifugeRecipe build(CombItem comb) {
            CentrifugeRecipe recipe = new CentrifugeRecipe(20, comb.getStackFromType(type, 1), products);
            add(recipe);
            return recipe;
        }
    }
}

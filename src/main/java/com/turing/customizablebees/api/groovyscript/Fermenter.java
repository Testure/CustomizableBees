package com.turing.customizablebees.api.groovyscript;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import forestry.api.recipes.IFermenterRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.factory.recipes.FermenterRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

public class Fermenter extends VirtualizedRegistry<IFermenterRecipe> {
    public Fermenter() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(RecipeManagers.fermenterManager::removeRecipe);
        restoreFromBackup().forEach(RecipeManagers.fermenterManager::addRecipe);
    }

    public void add(IFermenterRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        RecipeManagers.fermenterManager.addRecipe(recipe);
    }

    public IFermenterRecipe add(FluidStack input, FluidStack output, ItemStack catalyst, int value, float modifier) {
        FermenterRecipe recipe = new FermenterRecipe(catalyst, value, modifier, input.getFluid(), output);
        add(recipe);
        return recipe;
    }

    public boolean remove(IFermenterRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        RecipeManagers.fermenterManager.removeRecipe(recipe);
        return true;
    }

    public boolean removeByCatalyst(IIngredient stack) {
        Collection<IFermenterRecipe> recipes = new ArrayList<>(RecipeManagers.fermenterManager.recipes());
        if (recipes.removeIf(recipe -> {
            boolean found = !recipe.getResource().isEmpty() && stack.test(recipe.getResource());
            if (found) remove(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Fermenter recipe")
                .add("could not find recipe with catalyst %s", stack)
                .error()
                .post();
        return false;
    }

    public boolean removeByOutput(FluidStack stack) {
        Collection<IFermenterRecipe> recipes = new ArrayList<>(RecipeManagers.fermenterManager.recipes());
        if (recipes.removeIf(recipe -> {
            boolean found = recipe.getOutput() == stack.getFluid();
            if (found) remove(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Fermenter recipe")
                .add("could not find recipe with output %s", stack)
                .error()
                .post();
        return false;
    }

    public boolean removeByInput(FluidStack stack) {
        Collection<IFermenterRecipe> recipes = new ArrayList<>(RecipeManagers.fermenterManager.recipes());
        if (recipes.removeIf(recipe -> {
            boolean found = recipe.getFluidResource().isFluidEqual(stack);
            if (found) remove(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Fermenter recipe")
                .add("could not find recipe with input %s", stack)
                .error()
                .post();
        return false;
    }

    public void removeAll() {
        RecipeManagers.fermenterManager.recipes().forEach(this::addBackup);
        Collection<IFermenterRecipe> recipes = new ArrayList<>(RecipeManagers.fermenterManager.recipes());
        recipes.forEach(RecipeManagers.fermenterManager::removeRecipe);
    }

    public SimpleObjectStream<IFermenterRecipe> streamRecipes() {
        return new SimpleObjectStream<>(RecipeManagers.fermenterManager.recipes()).setRemover(this::remove);
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<IFermenterRecipe> {
        private int amount = 100;
        private float modifier = 1.0F;
        private IIngredient catalyst;

        public RecipeBuilder setModifier(float modifier) {
            this.modifier = Math.max(modifier, 0.01F);
            return this;
        }

        public RecipeBuilder setAmount(int amount) {
            this.amount = Math.max(amount, 1);
            return this;
        }

        public RecipeBuilder setCatalyst(IIngredient input) {
            this.catalyst = input;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Forestry Fermenter recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 0, 0);
            validateFluids(msg, 1, 1, 1, 1);
            msg.add(catalyst == null,"Expected Ingredient for catalyst but found none!");
        }

        @Override
        public @Nullable IFermenterRecipe register() {
            if (!validate()) return null;
            FermenterRecipe recipe;
            if (catalyst instanceof OreDictIngredient) recipe = new FermenterRecipe(((OreDictIngredient) catalyst).getOreDict(), amount, modifier, fluidOutput.get(0).getFluid(), fluidInput.get(0));
            else recipe = new FermenterRecipe(catalyst.getMatchingStacks()[0], amount, modifier, fluidOutput.get(0).getFluid(), fluidInput.get(0));
            add(recipe);
            return recipe;
        }
    }
}

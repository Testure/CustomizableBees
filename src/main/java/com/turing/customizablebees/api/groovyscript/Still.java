package com.turing.customizablebees.api.groovyscript;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import forestry.api.recipes.IStillRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.factory.recipes.StillRecipe;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

public class Still extends VirtualizedRegistry<IStillRecipe> {
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(RecipeManagers.stillManager::removeRecipe);
        restoreFromBackup().forEach(RecipeManagers.stillManager::addRecipe);
    }

    public IStillRecipe add(FluidStack input, FluidStack output, int time) {
        StillRecipe recipe = new StillRecipe(time, input, output);
        add(recipe);
        return recipe;
    }

    public void add(IStillRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        RecipeManagers.stillManager.addRecipe(recipe);
    }

    public boolean remove(IStillRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        RecipeManagers.stillManager.removeRecipe(recipe);
        return true;
    }

    public boolean removeByInput(FluidStack input) {
        Collection<IStillRecipe> recipes = new ArrayList<>(RecipeManagers.stillManager.recipes());
        if (recipes.removeIf(recipe -> {
            boolean found = recipe.getInput().isFluidEqual(input);
            if (found) remove(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Still recipe")
                .add("could not find recipe with input %s", input)
                .error()
                .post();
        return false;
    }

    public boolean removeByOutput(FluidStack output) {
        Collection<IStillRecipe> recipes = new ArrayList<>(RecipeManagers.stillManager.recipes());
        if (recipes.removeIf(recipe -> {
            boolean found = recipe.getOutput().isFluidEqual(output);
            if (found) remove(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Still recipe")
                .add("could not find recipe with output %s", output)
                .error()
                .post();
        return false;
    }

    public boolean removeByInputAndOutput(FluidStack input, FluidStack output) {
        Collection<IStillRecipe> recipes = new ArrayList<>(RecipeManagers.stillManager.recipes());
        if (recipes.removeIf(recipe -> {
            boolean found = recipe.getInput().isFluidEqual(input) && recipe.getOutput().isFluidEqual(output);
            if (found) remove(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Still recipe")
                .add("could not find recipe with input %s and output %s", input, output)
                .error()
                .post();
        return false;
    }

    public void removeAll() {
        RecipeManagers.stillManager.recipes().forEach(this::addBackup);
        Collection<IStillRecipe> recipes = new ArrayList<>(RecipeManagers.stillManager.recipes());
        recipes.forEach(RecipeManagers.stillManager::removeRecipe);
    }

    public SimpleObjectStream<IStillRecipe> streamRecipes() {
        return new SimpleObjectStream<>(RecipeManagers.stillManager.recipes()).setRemover(this::remove);
    }

    public class RecipeBuilder implements IRecipeBuilder<IStillRecipe> {
        private int time = 100;
        private FluidStack input;
        private FluidStack output;

        public RecipeBuilder fluidInput(FluidStack stack) {
            this.input = stack;
            return this;
        }

        public RecipeBuilder fluidOutput(FluidStack stack) {
            this.output = stack;
            return this;
        }

        public RecipeBuilder setTime(int time) {
            this.time = Math.max(time, 0);
            return this;
        }

        private void validate(GroovyLog.Msg msg) {
            msg.add(input == null || input.amount <= 0, "Expected at least 1Mb of output fluid, got " + (input != null ? input.amount : "null"));
            msg.add(output == null || output.amount <= 0, "Expected at least 1Mb of output fluid, got " + (output != null ? output.amount : "null"));
            msg.add(time < 0, "Recipe time must be at least 0, got " + time);
        }

        private String getErrorMsg() {
            return "Error adding Forestry Still recipe";
        }

        @Override
        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg(this.getErrorMsg()).error();
            this.validate(msg);
            return !msg.postIfNotEmpty();
        }

        @Override
        public @Nullable IStillRecipe register() {
            if (!validate()) return null;
            StillRecipe recipe = new StillRecipe(time, input, output);
            add(recipe);
            return recipe;
        }
    }
}

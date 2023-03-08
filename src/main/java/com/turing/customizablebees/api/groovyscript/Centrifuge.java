package com.turing.customizablebees.api.groovyscript;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.factory.recipes.CentrifugeRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.*;

public class Centrifuge extends VirtualizedRegistry<ICentrifugeRecipe> {
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(RecipeManagers.centrifugeManager::removeRecipe);
        restoreFromBackup().forEach(RecipeManagers.centrifugeManager::addRecipe);
    }

    public ICentrifugeRecipe add(IIngredient input, int time, Map<ItemStack, Float> outputs) {
        CentrifugeRecipe recipe = new CentrifugeRecipe(time, input.getMatchingStacks()[0], outputs);
        add(recipe);
        return recipe;
    }

    public void add(ICentrifugeRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        RecipeManagers.centrifugeManager.addRecipe(recipe);
    }

    public boolean remove(ICentrifugeRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        RecipeManagers.centrifugeManager.removeRecipe(recipe);
        return true;
    }

    public boolean removeByInput(IIngredient input) {
        Collection<ICentrifugeRecipe> recipes = new ArrayList<>(RecipeManagers.centrifugeManager.recipes());
        if (recipes.removeIf(recipe -> {
            boolean found = input.test(recipe.getInput());
            if (found) remove(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Centrifuge recipe")
                .add("could not find recipe with input %s", input)
                .error()
                .post();
        return false;
    }

    public boolean removeByOutputs(ItemStack... outputs) {
        List<ItemStack> outputList = Arrays.asList(outputs);
        Collection<ICentrifugeRecipe> recipes = new ArrayList<>(RecipeManagers.centrifugeManager.recipes());
        if (recipes.removeIf(recipe -> {
            boolean found = true;
            for (ItemStack output : recipe.getAllProducts().keySet()) {
                if (!outputList.contains(output)) {
                    found = false;
                    break;
                }
            }
            if (found) outputList.clear();

            if (found) remove(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Centrifuge recipe")
                .add("could not find recipe with outputs %s", outputList)
                .error()
                .post();
        return false;
    }

    public boolean removeByOutput(ItemStack output) {
        return removeByOutputs(output);
    }

    public boolean removeByInputAndOutputs(IIngredient input, ItemStack... outputs) {
        Collection<ICentrifugeRecipe> recipes = new ArrayList<>(RecipeManagers.centrifugeManager.recipes());
        if (recipes.removeIf(recipe -> {
            boolean found = input.test(recipe.getInput());
            if (found) {
                List<ItemStack> outputList = Arrays.asList(outputs);
                for (ItemStack output : recipe.getAllProducts().keySet()) {
                    if (!outputList.contains(output)) {
                        found = false;
                        break;
                    }
                }
                outputList.clear();
            }

            if (found) remove(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Centrifuge recipe")
                .add("could not find recipe with input %s and outputs %s", input, outputs)
                .error()
                .post();
        return false;
    }

    public boolean removeByInputAndOutput(IIngredient input, ItemStack output) {
        return removeByInputAndOutputs(input, output);
    }

    public void removeAll() {
        RecipeManagers.centrifugeManager.recipes().forEach(this::addBackup);
        Collection<ICentrifugeRecipe> set = new ArrayList<>(RecipeManagers.centrifugeManager.recipes());
        set.forEach(RecipeManagers.centrifugeManager::removeRecipe);
    }

    public SimpleObjectStream<ICentrifugeRecipe> streamRecipes() {
        return new SimpleObjectStream<>(RecipeManagers.centrifugeManager.recipes()).setRemover(this::remove);
    }

    public class RecipeBuilder implements IRecipeBuilder<ICentrifugeRecipe> {
        private ItemStack input;
        private Map<ItemStack, Float> outputs;
        private int time = 20;

        public RecipeBuilder output(ItemStack stack, float chance) {
            addOutputMap();
            this.outputs.put(stack, MathHelper.clamp(chance, 0.01F, 1.0F));
            return this;
        }

        public RecipeBuilder output(ItemStack stack) {
            return output(stack, 1.0F);
        }

        public RecipeBuilder setTime(int time) {
            this.time = Math.max(time, 0);
            return this;
        }

        public RecipeBuilder input(IIngredient input) {
            this.input = input.getMatchingStacks()[0];
            return this;
        }

        private void addOutputMap() {
            if (this.outputs == null) {
                this.outputs = new HashMap<>();
            }
        }

        private void validate(GroovyLog.Msg msg) {
            msg.add(input == null || input.isEmpty(), "Must have an input ItemStack but didn't find any!");
            msg.add(time < 0, "Recipe time must be at least 0, got " + time);
            msg.add(outputs == null || outputs.isEmpty(), "Must have at least 1 output but didn't find any!");
            outputs.forEach((stack, chance) -> {
                msg.add(stack == null || stack.isEmpty(), "Output item cannot be empty");
                msg.add(chance < 0.01F || chance > 1.0F, "Chance must be between 0.01 and 1, got " + chance);
            });
        }

        private String getErrorMsg() {
            return "Error adding Forestry Centrifuge recipe";
        }

        @Override
        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg(this.getErrorMsg()).error();
            this.validate(msg);
            return !msg.postIfNotEmpty();
        }

        @Override
        public @Nullable ICentrifugeRecipe register() {
            if (!validate()) return null;
            CentrifugeRecipe recipe = new CentrifugeRecipe(this.time, this.input, this.outputs);
            add(recipe);
            return recipe;
        }
    }
}

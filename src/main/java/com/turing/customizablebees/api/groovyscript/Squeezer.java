package com.turing.customizablebees.api.groovyscript;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import forestry.api.recipes.ISqueezerRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.factory.recipes.SqueezerRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Squeezer extends VirtualizedRegistry<ISqueezerRecipe> {
    public Squeezer() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(RecipeManagers.squeezerManager::removeRecipe);
        restoreFromBackup().forEach(RecipeManagers.squeezerManager::addRecipe);
    }

    public ISqueezerRecipe add(ItemStack remnant, FluidStack output, int time, float remnantChance, IIngredient... inputs) {
        NonNullList<ItemStack> inputList = NonNullList.create();
        for (IIngredient input : inputs) inputList.add(input.getMatchingStacks()[0]);
        SqueezerRecipe recipe = new SqueezerRecipe(time, inputList, output, remnant, remnantChance);
        add(recipe);
        return recipe;
    }

    public void add(ISqueezerRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        RecipeManagers.squeezerManager.addRecipe(recipe);
    }

    public boolean remove(ISqueezerRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        RecipeManagers.squeezerManager.removeRecipe(recipe);
        return true;
    }

    public boolean removeByOutput(FluidStack output) {
        Collection<ISqueezerRecipe> recipes = new ArrayList<>(RecipeManagers.squeezerManager.recipes());
        if (recipes.removeIf(recipe -> {
            boolean found = recipe.getFluidOutput().isFluidEqual(output);
            if (found) remove(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Squeezer recipe")
                .add("could not find recipe with output %s", output)
                .error()
                .post();
        return false;
    }

    public boolean removeByInputs(IIngredient... inputs) {
        List<ItemStack> inputList = Arrays.stream(inputs).map(ingredient -> ingredient.getMatchingStacks()[0]).collect(Collectors.toList());
        Collection<ISqueezerRecipe> recipes = new ArrayList<>(RecipeManagers.squeezerManager.recipes());
        if (recipes.removeIf(recipe -> {
            boolean found = true;
            for (ItemStack input : recipe.getResources()) {
                if (!inputList.contains(input)) {
                    found = false;
                    break;
                }
            }
            if (found) inputList.clear();

            if (found) remove(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Squeezer recipe")
                .add("could not find recipe with inputs %s", inputList)
                .error()
                .post();
        return false;
    }

    public boolean removeByInput(IIngredient input) {
        return removeByInputs(input);
    }

    public boolean removeByInputsAndOutput(FluidStack output, IIngredient... inputs) {
        Collection<ISqueezerRecipe> recipes = new ArrayList<>(RecipeManagers.squeezerManager.recipes());
        if (recipes.removeIf(recipe -> {
            boolean found = recipe.getFluidOutput().isFluidEqual(output);
            if (found) {
                List<ItemStack> inputList = Arrays.stream(inputs).map(ingredient -> ingredient.getMatchingStacks()[0]).collect(Collectors.toList());
                for (ItemStack input : recipe.getResources()) {
                    if (!inputList.contains(input)) {
                        found = false;
                        break;
                    }
                }
                inputList.clear();
            }

            if (found) remove(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Squeezer recipe")
                .add("could not find recipe with output %s and inputs %s", output, inputs)
                .error()
                .post();
        return false;
    }

    public void removeAll() {
        RecipeManagers.squeezerManager.recipes().forEach(this::addBackup);
        Collection<ISqueezerRecipe> set = new ArrayList<>(RecipeManagers.squeezerManager.recipes());
        set.forEach(RecipeManagers.squeezerManager::removeRecipe);
    }

    public SimpleObjectStream<ISqueezerRecipe> streamRecipes() {
        return new SimpleObjectStream<>(RecipeManagers.squeezerManager.recipes()).setRemover(this::remove);
    }

    public class RecipeBuilder implements IRecipeBuilder<ISqueezerRecipe> {
        private NonNullList<ItemStack> inputs;
        private FluidStack output;
        private int time = 20;
        private float chance;
        private ItemStack remnant = ItemStack.EMPTY;

        public RecipeBuilder input(IIngredient stack) {
            if (stack == null || stack.getMatchingStacks().length < 1) return this;
            addInputList();
            this.inputs.add(stack.getMatchingStacks()[0]);
            return this;
        }

        public RecipeBuilder fluidOutput(FluidStack stack) {
            this.output = stack;
            return this;
        }

        public RecipeBuilder setRemnantItem(ItemStack stack) {
            return setRemnantItem(stack, 1.0F);
        }

        public RecipeBuilder setRemnantItem(ItemStack stack, float chance) {
            this.remnant = stack != null ? stack : ItemStack.EMPTY;
            this.chance = Math.max(Math.min(chance, 1.0F), 0.01F);
            return this;
        }

        public RecipeBuilder setTime(int time) {
            this.time = Math.max(time, 0);
            return this;
        }

        private void addInputList() {
            if (this.inputs == null) {
                this.inputs = NonNullList.create();
            }
        }

        private void validate(GroovyLog.Msg msg) {
            msg.add(remnant == null, "Remnant ItemStack must not be null!");
            msg.add(inputs == null || inputs.isEmpty(), "Must have at least 1 input but didn't find any!");
            msg.add(output == null || output.amount <= 0, "Expected at least 1Mb of output fluid, got " + (output != null ? output.amount : "null"));
            msg.add(time < 0, "Recipe time must be at least 0, got " + time);
            msg.add(chance < 0.01F || chance > 1.0F, "Chance must be between 0.01 and 1, got " + chance);
            inputs.forEach(stack -> msg.add(stack.isEmpty(), "Input stack cannot be empty"));
        }

        private String getErrorMsg() {
            return "Error adding Forestry Squeezer recipe";
        }

        @Override
        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg(this.getErrorMsg()).error();
            this.validate(msg);
            return !msg.postIfNotEmpty();
        }

        @Override
        public @Nullable ISqueezerRecipe register() {
            if (!validate()) return null;
            SqueezerRecipe recipe = new SqueezerRecipe(time, inputs, output, remnant, chance);
            add(recipe);
            return recipe;
        }
    }
}

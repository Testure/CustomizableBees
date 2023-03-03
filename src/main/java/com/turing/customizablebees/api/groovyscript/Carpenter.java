package com.turing.customizablebees.api.groovyscript;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.factory.recipes.CarpenterRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.*;

public class Carpenter extends VirtualizedRegistry<ICarpenterRecipe> {
    public Carpenter() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(RecipeManagers.carpenterManager::removeRecipe);
        restoreFromBackup().forEach(RecipeManagers.carpenterManager::addRecipe);
    }

    public ICarpenterRecipe add(ItemStack product, ItemStack box, @Nullable FluidStack fluid, int time, Object... materials) {
        CarpenterRecipe recipe = new CarpenterRecipe(time, fluid, box, ShapedRecipeCustom.createShapedRecipe(product, materials));
        add(recipe);
        return recipe;
    }

    public void add(ICarpenterRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        RecipeManagers.carpenterManager.addRecipe(recipe);
    }

    public boolean remove(ICarpenterRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        RecipeManagers.carpenterManager.removeRecipe(recipe);
        return true;
    }

    public boolean removeByOutput(ItemStack output) {
        Collection<ICarpenterRecipe> recipes = new ArrayList<>(RecipeManagers.carpenterManager.recipes());
        if (recipes.removeIf(recipe -> {
            boolean found = recipe.getCraftingGridRecipe().getOutput().isItemEqual(output) && recipe.getCraftingGridRecipe().getOutput().getCount() == output.getCount();
            if (found) remove(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Carpenter recipe")
                .add("could not find recipe with output %s", output)
                .error()
                .post();
        return false;
    }

    public boolean removeByBoxInput(IIngredient input) {
        Collection<ICarpenterRecipe> recipes = new ArrayList<>(RecipeManagers.carpenterManager.recipes());
        if (recipes.removeIf(recipe -> {
            boolean found = input.test(recipe.getBox());
            if (found) remove(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Carpenter recipe")
                .add("could not find recipe with box %s", input)
                .error()
                .post();
        return false;
    }

    public boolean removeByFluidInput(FluidStack input) {
        Collection<ICarpenterRecipe> recipes = new ArrayList<>(RecipeManagers.carpenterManager.recipes());
        if (recipes.removeIf(recipe -> {
            boolean found = recipe.getFluidResource() != null && recipe.getFluidResource().isFluidEqual(input);
            if (found) remove(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Carpenter recipe")
                .add("could not find recipe with fluid input %s", input)
                .error()
                .post();
        return false;
    }

    public boolean removeByInputs(IIngredient box, FluidStack fluid) {
        Collection<ICarpenterRecipe> recipes = new ArrayList<>(RecipeManagers.carpenterManager.recipes());
        if (recipes.removeIf(recipe -> {
            boolean found = recipe.getFluidResource() != null && recipe.getFluidResource().isFluidEqual(fluid) && box.test(recipe.getBox());
            if (found) remove(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Carpenter recipe")
                .add("could not find recipe with box %s and fluid input %s", box, fluid)
                .error()
                .post();
        return false;
    }

    public void removeAll() {
        RecipeManagers.carpenterManager.recipes().forEach(this::addBackup);
        Collection<ICarpenterRecipe> recipes = new ArrayList<>(RecipeManagers.carpenterManager.recipes());
        recipes.forEach(RecipeManagers.carpenterManager::removeRecipe);
    }

    public SimpleObjectStream<ICarpenterRecipe> streamRecipes() {
        return new SimpleObjectStream<>(RecipeManagers.carpenterManager.recipes()).setRemover(this::remove);
    }

    public class RecipeBuilder implements IRecipeBuilder<ICarpenterRecipe> {
        private FluidStack fluid;
        private int time;
        private ItemStack box = ItemStack.EMPTY;
        private ItemStack output;
        private String[] pattern;
        private Map<Character, IIngredient> inputs;

        public RecipeBuilder setTime(int time) {
            this.time = Math.max(time, 0);
            return this;
        }

        public RecipeBuilder fluidInput(@Nullable FluidStack fluid) {
            this.fluid = fluid;
            return this;
        }

        public RecipeBuilder output(ItemStack stack) {
            this.output = stack;
            return this;
        }

        public RecipeBuilder setBoxItem(@Nullable ItemStack box) {
            this.box = box != null ? box : ItemStack.EMPTY;
            return this;
        }

        public RecipeBuilder setRecipePattern(String line) {
            this.pattern = new String[]{line};
            return this;
        }

        public RecipeBuilder setRecipePattern(String line1, String line2) {
            this.pattern = new String[]{line1, line2};
            return this;
        }

        public RecipeBuilder setRecipePattern(String line1, String line2, String line3) {
            this.pattern = new String[]{line1, line2, line3};
            return this;
        }

        public RecipeBuilder assignInput(String s, IIngredient input) {
            char c = s.charAt(0);
            if (c == ' ') return this;
            addInputMap();
            this.inputs.put(c, input);
            return this;
        }

        private void addInputMap() {
            if (this.inputs == null) {
                this.inputs = new HashMap<>();
            }
        }

        private void validate(GroovyLog.Msg msg) {
            msg.add(box == null, "Box ItemStack must not be null!");
            msg.add(pattern == null || pattern.length > 3 || pattern.length < 1, "Must have a valid input recipe, got " + Arrays.toString(pattern));
            msg.add(output == null || output.isEmpty(), "Must have at least 1 output but didn't find any!");
            msg.add(fluid != null && fluid.amount < 0, "Fluid must have at least 1Mb!");
            msg.add(time < 0, "Recipe time must be at least 0, got " + time);
            msg.add(inputs == null || inputs.isEmpty(), "Must have at least 1 input character mapped, got " + inputs);
            Arrays.stream(pattern).forEach(line -> {
                msg.add(line.length() > 3 || line.length() < 1, "Pattern line must have a length between 1 and 3!");
                for (int c = 0; c < line.length(); c++)
                    msg.add(line.charAt(c) != ' ' && (inputs == null || inputs.isEmpty() || !inputs.containsKey(line.charAt(c))), "Pattern character '" + line.charAt(c) + "' has no defined input!");
            });
            int empty = 0;
            for (String line : pattern) if (line.equals("") || line.equals(" ") || line.equals("  ") || line.equals("   ")) empty++;
            msg.add(empty == pattern.length, "Cannot have empty pattern string!");
        }

        private String getErrorMsg() {
            return "Error adding Forestry Carpenter recipe";
        }

        @Override
        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg(this.getErrorMsg()).error();
            this.validate(msg);
            return !msg.postIfNotEmpty();
        }

        @Override
        public @Nullable ICarpenterRecipe register() {
            if (!validate()) return null;
            List<Object> argList = new ArrayList<>(Arrays.asList(pattern));
            for (Map.Entry<Character, IIngredient> entry : inputs.entrySet()) {
                argList.add(entry.getKey());
                if (entry.getValue() instanceof OreDictIngredient) argList.add(((OreDictIngredient) entry.getValue()).getOreDict());
                else argList.add(entry.getValue().getMatchingStacks()[0]);
            }

            CarpenterRecipe recipe = new CarpenterRecipe(time, fluid, box, ShapedRecipeCustom.createShapedRecipe(output, argList.toArray()));
            add(recipe);
            return recipe;
        }
    }
}

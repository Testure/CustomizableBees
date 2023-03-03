package com.turing.customizablebees.api.groovyscript;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.factory.recipes.FabricatorRecipe;
import forestry.factory.recipes.FabricatorSmeltingRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.*;

public class ThermionicFabricator extends VirtualizedRegistry<IFabricatorRecipe> {
    public final Smelting smelting = new Smelting();

    public ThermionicFabricator() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(RecipeManagers.fabricatorManager::removeRecipe);
        restoreFromBackup().forEach(RecipeManagers.fabricatorManager::addRecipe);
    }

    public IFabricatorRecipe add(ItemStack output, @Nullable ItemStack catalyst, FluidStack fluid, NonNullList<NonNullList<ItemStack>> inputs, NonNullList<String> oreDicts, int h, int w) {
        FabricatorRecipe recipe = new FabricatorRecipe(catalyst != null ? catalyst : ItemStack.EMPTY, fluid, output, inputs, oreDicts, w, h);
        add(recipe);
        return recipe;
    }

    public void add(IFabricatorRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        RecipeManagers.fabricatorManager.addRecipe(recipe);
    }

    public boolean remove(IFabricatorRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        RecipeManagers.fabricatorManager.removeRecipe(recipe);
        return true;
    }

    public boolean removeByOutput(IIngredient output) {
        Collection<IFabricatorRecipe> recipes = new ArrayList<>(RecipeManagers.fabricatorManager.recipes());
        if (recipes.removeIf(recipe -> {
            boolean found = output.test(recipe.getRecipeOutput());
            if (found) remove(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Thermionic Fabricator recipe")
                .add("could not find recipe with output %s", output)
                .error()
                .post();
        return false;
    }

    public boolean removeByFluidInput(FluidStack stack) {
        Collection<IFabricatorRecipe> recipes = new ArrayList<>(RecipeManagers.fabricatorManager.recipes());
        if (recipes.removeIf(recipe -> {
            boolean found = recipe.getLiquid().isFluidEqual(stack);
            if (found) remove(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Thermionic Fabricator recipe")
                .add("could not find recipe with fluid input %s", stack)
                .error()
                .post();
        return false;
    }

    public void removeAll() {
        RecipeManagers.fabricatorManager.recipes().forEach(this::addBackup);
        Collection<IFabricatorRecipe> recipes = new ArrayList<>(RecipeManagers.fabricatorManager.recipes());
        recipes.forEach(RecipeManagers.fabricatorManager::removeRecipe);
    }

    public SimpleObjectStream<IFabricatorRecipe> streamRecipes() {
        return new SimpleObjectStream<>(RecipeManagers.fabricatorManager.recipes()).setRemover(this::remove);
    }

    public class RecipeBuilder implements IRecipeBuilder<IFabricatorRecipe> {
        private FluidStack fluid;
        private ItemStack catalyst = ItemStack.EMPTY;
        private ItemStack output;
        private String[] pattern;
        private Map<Character, IIngredient> inputs;

        public RecipeBuilder output(ItemStack stack) {
            this.output = stack;
            return this;
        }

        public RecipeBuilder fluidInput(FluidStack stack) {
            this.fluid = stack;
            return this;
        }

        public RecipeBuilder setCatalyst(@Nullable ItemStack stack) {
            this.catalyst = stack != null ? stack : ItemStack.EMPTY;
            if (stack != null) this.catalyst.setCount(1);
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
            msg.add(catalyst == null, "Catalyst ItemStack must not be null!");
            msg.add(pattern == null || pattern.length > 3 || pattern.length < 1, "Must have a valid input recipe, got " + Arrays.toString(pattern));
            msg.add(output == null || output.isEmpty(), "Must have at least 1 output but didn't find any!");
            msg.add(fluid != null && fluid.amount < 0, "Fluid must have at least 1Mb!");
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
            return "Error adding Forestry Thermionic Fabricator recipe";
        }

        @Override
        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg(this.getErrorMsg()).error();
            this.validate(msg);
            return !msg.postIfNotEmpty();
        }

        @Override
        public @Nullable IFabricatorRecipe register() {
            if (!validate()) return null;
            List<Object> argList = new ArrayList<>(Arrays.asList(pattern));
            for (Map.Entry<Character, IIngredient> entry : inputs.entrySet()) {
                argList.add(entry.getKey());
                if (entry.getValue() instanceof OreDictIngredient) argList.add(((OreDictIngredient) entry.getValue()).getOreDict());
                else argList.add(entry.getValue().getMatchingStacks()[0]);
            }
            ShapedRecipeCustom custom = ShapedRecipeCustom.createShapedRecipe(output, argList.toArray());

            FabricatorRecipe recipe = new FabricatorRecipe(catalyst, fluid, output, custom.getRawIngredients(), custom.getOreDicts(), custom.getWidth(), custom.getHeight());
            add(recipe);
            return recipe;
        }
    }

    public static class Smelting extends VirtualizedRegistry<IFabricatorSmeltingRecipe> {
        public Smelting() {
            super();
        }

        @Override
        @GroovyBlacklist
        public void onReload() {
            removeScripted().forEach(RecipeManagers.fabricatorSmeltingManager::removeRecipe);
            restoreFromBackup().forEach(RecipeManagers.fabricatorSmeltingManager::addRecipe);
        }

        public void add(IFabricatorSmeltingRecipe recipe) {
            if (recipe == null) return;
            addScripted(recipe);
            RecipeManagers.fabricatorSmeltingManager.addRecipe(recipe);
        }

        public boolean remove(IFabricatorSmeltingRecipe recipe) {
            if (recipe == null) return false;
            addBackup(recipe);
            RecipeManagers.fabricatorSmeltingManager.removeRecipe(recipe);
            return true;
        }

        public IFabricatorSmeltingRecipe addSmelting(IIngredient input, FluidStack output, int meltingPoint) {
            FabricatorSmeltingRecipe recipe = new FabricatorSmeltingRecipe(input.getMatchingStacks()[0], output, meltingPoint);
            add(recipe);
            return recipe;
        }

        public boolean removeByInput(IIngredient stack) {
            Collection<IFabricatorSmeltingRecipe> recipes = new ArrayList<>(RecipeManagers.fabricatorSmeltingManager.recipes());
            if (recipes.removeIf(recipe -> {
                boolean found = stack.test(recipe.getResource());
                if (found) remove(recipe);
                return found;
            })) return true;

            GroovyLog.msg("Error removing Forestry Thermionic Fabricator Smelting recipe")
                    .add("Could not find recipe with input %s", stack)
                    .error()
                    .post();
            return false;
        }

        public boolean removeByOutput(FluidStack stack) {
            Collection<IFabricatorSmeltingRecipe> recipes = new ArrayList<>(RecipeManagers.fabricatorSmeltingManager.recipes());
            if (recipes.removeIf(recipe -> {
                boolean found = recipe.getProduct().isFluidEqual(stack);
                if (found) remove(recipe);
                return found;
            })) return true;

            GroovyLog.msg("Error removing Forestry Thermionic Fabricator Smelting recipe")
                    .add("Could not find recipe with output %s", stack)
                    .error()
                    .post();
            return false;
        }

        public void removeAll() {
            RecipeManagers.fabricatorSmeltingManager.recipes().forEach(this::addBackup);
            Collection<IFabricatorSmeltingRecipe> recipes = new ArrayList<>(RecipeManagers.fabricatorSmeltingManager.recipes());
            recipes.forEach(RecipeManagers.fabricatorSmeltingManager::removeRecipe);
        }

        public SimpleObjectStream<IFabricatorSmeltingRecipe> streamRecipes() {
            return new SimpleObjectStream<>(RecipeManagers.fabricatorSmeltingManager.recipes()).setRemover(this::remove);
        }
    }
}

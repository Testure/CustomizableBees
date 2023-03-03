# Forestry Thermionic Fabricator
## Adding Recipes
Like other recipe types, the Thermionic Fabricator uses a recipe builder.
```groovy
mods.forestry.ThermionicFabricator.recipeBuilder()
```
Set the recipe patter: (requires 1-3 strings each with a length of 1-3 characters (each string is one slot downwards in the crafting grid))
```groovy
.setRecipePattern(String...)
```
Assign an Ingredient to a pattern character:
```groovy
.assignInput(String character, IIngredient item)
```
Set the fluid input: (requires exactly 1)
```groovy
.fluidInput(FluidStack)
```
Set the catalyst item: (catalyst is not consumed)
```groovy
.setCatalyst(ItemStack) // can be null to require no catalyst
```
Set the output item: (requires exactly 1)
```groovy
.output(ItemStack)
```
Register recipe: (returns a `IFabricatorRecipe`)
```groovy
.register()
```
### Example
```groovy
mods.forestry.ThermionicFabricator.recipeBuilder()
        .setRecipePattern(
                "#C#",
                "CIC",
                "#C#"
        )
        .assignInput('C', item('forestry:ingot_copper'))
        .assignInput('I', item('minecraft:iron_ingot'))
        .assignInput('#', ore('cobblestone'))
        .output(item('minecraft:bedrock') * 5)
        .fluidInput(fluid('for.honey') * 50)
        .setCatalyst(item('minecraft:stone'))
        .register()
```
## Removing Recipes
Remove this exact recipe:
```groovy
mods.forestry.ThermionicFabricator.remove(IFabricatorRecipe)
```
Remove all recipes that use the given fluid:
```groovy
mods.forestry.ThermionicFabricator.removeByFluidInput(FluidStack)
```
Remove all recipes that create the given output:
```groovy
mods.forestry.ThermionicFabricator.removeByOutput(IIngredient)
```
### Example
```groovy
mods.forestry.ThermionicFabricator.removeByFluidInput(fluid('glass')) // remove recipes that use liquid glass (aka all default recipes)
mods.forestry.ThermionicFabricator.removeByOutput(item('forestry:thermionic_tubes', 1)) // remove recipes that create tin electron tubes
```
## Adding Smelts
You can also add fuels that smelt into different fluids inside the Thermionic Fabricator.
```groovy
mods.forestry.ThermionicFabricator.Smelting.addSmelting(ItemStack item, FluidStack fluid, int requiredTemp)
```
### Example
```groovy
mods.forestry.ThermionicFabricator.Smelting.addSmelting(item('forestry:ingot_bronze'), fluid('for.honey') * 5, 4000)
```
## Removing Smelts
```groovy
mods.forestry.ThermionicFabricator.Smelting.removeByInput(IIngredient)
```
```groovy
mods.forestry.ThermionicFabricator.Smelting.removeByOutput(FluidStack)
```
### Example
```groovy
mods.forestry.ThermionicFabricator.Smelting.removeByInput(ore('paneGlass')) // remove glass panes from smelting
mods.forestry.ThermionicFabricator.Smelting.removeByOutput(fluid('glass')) // remove liquid glass
```
# Forestry Squeezer
## Adding Recipes
Like other recipe types, the Squeezer uses a recipe builder.
```groovy
mods.forestry.Squeezer.recipeBuilder()
```
Add an input item: (requires 1-9)
```groovy
.input(IIngredient)
```
Set the fluid output: (requires exactly 1)
```groovy
.fluidOutput(FluidStack)
```
Set a guaranteed remnant item:
```groovy
.setRemnantItem(ItemStack)
```
Set a chanced remnant item:
```groovy
.setRemnantItem(ItemStack, float) // 0.01 is 1% / 1.0 is 100%
```
Set the time the recipe takes:
```groovy
.setTime(int) // default is 20
```
Register recipe: (returns a `ISqueezerRecipe`)
```groovy
.register()
```
### Example
```groovy
mods.forestry.Squeezer.recipeBuilder()
    .input(item('minecraft:emerald'))
    .input(item('minecraft:diamond') * 4)
    .fluidOutput(fluid('lava') * 1000)
    .setRemnantItem(item('minecraft:coal'), 0.2F)
    .setTime(60)
    .register()
```
## Removing Recipes
Remove this exact recipe:
```groovy
mods.forestry.Squeezer.remove(ISqueezerRecipe)
```
Remove all recipes that result in the given fluid:
```groovy
mods.forestry.Squeezer.removeByOutput(FluidStack)
```
Remove all recipes that take the given inputs:
```groovy
mods.forestry.Squeezer.removeByInputs(IIngredient...)
```
Remove all recipes that create the given fluid using the given inputs:
```groovy
mods.forestry.Squeezer.removeByInputsAndOutput(FluidStack output, IIngredient... inputs)
```
### Example
```groovy
mods.forestry.Squeezer.removeByOutput(fluid('lava')) // remove recipes that create lava
mods.forestry.Squeezer.removeByInputs(item('minecraft:snowball'), item('forestry:crafting_material', 5)) // remove recipes that use a snowball and ice shards
mods.forestry.Squeezer.removeByInputsAndOutput(fluid('seed.oil'), item('minecraft:wheat_seeds')) // remove recipes that create seed oil but only if they use wheat seeds
```
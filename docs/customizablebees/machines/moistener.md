# Forestry Moistener
## Adding Recipes
Like other recipe types, the Moistener uses a recipe builder.
```groovy
mods.forestry.Moistener.recipeBuilder()
```
Set the input item: (requires exactly 1)
```groovy
.input(IIngredient)
```
Set the output item: (requires exactly 1)
```groovy
.output(ItemStack)
```
Set the time the recipe takes:
```groovy
.setTime(int) // default is 20
```
Register recipe: (returns a `IMoistenerRecipe`)
```groovy
.register()
```
### Example
```groovy
mods.forestry.Moistener.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('forestry:ingot_bronze'))
    .setTime(60)
    .register()
```
## Removing Recipes
Remove this exact recipe:
```groovy
mods.forestry.Moistener.remove(IMoistenerRecipe)
```
Remove all recipes that use the given input:
```groovy
mods.forestry.Moistener.removeByInput(IIngredient)
```
Remove all recipes that create the given output:
```groovy
mods.forestry.Moistener.removeByOutput(IIngredient)
```
Remove all recipes that create the given output with the given input:
```groovy
mods.forestry.Moistener.removeByInputAndOutput(IIngredient input, IIngredient output)
```
### Example
```groovy
mods.forestry.Moistener.removeByInput(item('minecraft:wheat_seeds')) // remove recipes that use seeds
mods.forestry.Moistener.removeByOutput(item('minecraft:stonebrick', 1)) // remove recipes that create mossy stone bricks
mods.forestry.Moistener.removeByInputAndOutput(item('minecraft:cobblestone'), item('minecraft:mossy_cobblestone')) // remove recipes that turn cobblestone into mossy cobblestone
```
## Adding Fuels
You can also add/remove fuels for the Moistener.
```groovy
mods.forestry.Moistener.Fuel.addFuel(IIngredient fuel, ItemStack byproduct, int value, int stage)
```
### Example
```groovy
mods.forestry.Moistener.Fuel.addFuel(item('minecraft:diamond'), item('minecraft:coal'), 80, 3)
```
## Removing Fuels
```groovy
mods.forestry.Moistener.Fuel.removeFuel(IIngredient)
```
### Example
```groovy
mods.forestry.Moistener.Fuel.removeFuel(item('minecraft:wheat'))
```
# Forestry Centrifuge
## Adding Recipes
Like other recipe types, the Centrifuge uses a recipe builder.
```groovy
mods.forestry.Centrifuge.recipeBuilder()
```
Set the input item: (requires exactly 1)
```groovy
.input(IIngredient)
```
Add a guaranteed output: (requires 1-6)
```groovy
.output(ItemStack)
```
Add a chanced output: (requires 1-6)
```groovy
.output(ItemStack, float) // 0.01 = 1% / 1.0 = 100%
```
Set the time the recipe takes:
```groovy
.setTime(int) // default is 20
```
Register recipe: (returns a `ICentrifugeRecipe`)
```groovy
.register()
```
### Example
```groovy
mods.forestry.Centrifuge.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:coal') * 5)
    .output(item('minecraft:emerald') * 2, 0.2F)
    .setTime(80)
    .register()
```
## Removing Recipes
Remove this exact recipe:
```groovy
mods.forestry.Centrifuge.remove(ICentrifugeRecipe)
```
Remove all recipes using the given item as the input:
```groovy
mods.forestry.Centrifuge.removeByInput(IIngredient)
```
Remove all recipes that create the given outputs:
```groovy
mods.forestry.Centrifuge.removeByOutputs(ItemStack...)
```
Remove all recipes that create the given outputs with the given input:
```groovy
mods.forestry.Centrifuge.removeByInputAndOutputs(IIngredient input, ItemStack... outputs)
```
### Example
```groovy
mods.forestry.Centrifuge.removeByInput(item('forestry:propolis', 3)) // removes recipes that use a silky propolis
mods.forestry.Centrifuge.removeByOutputs(item('forestry:honeydew'), item('forestry:beeswax')) // removes recipes that create honeydew AND beeswax
mods.forestry.Centrifuge.removeByInputAndOutputs(item('forestry:bee_combs', 5), item('forestry:honey_drop')) // removes recipes that use a dripping comb and create a honey drop
```
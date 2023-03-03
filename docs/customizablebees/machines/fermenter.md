# Forestry Fermenter
## Adding Recipes
Like other recipe types, the Fermenter uses a recipe builder.
```groovy
mods.forestry.Fermenter.recipeBuilder()
```
Adding an input fluid: (requires exactly 1)
```groovy
.fluidInput(FluidStack)
```
Adding an output fluid: (requires exactly 1)
```groovy
.fluidOutput(FluidStack)
```
Set the amount of input fluid needed: (the FluidStack amount is ignored in favor of this)
```groovy
.setAmount(int) // amount in Mb, the default is 100
```
Set the fluid ratio: (the amount of fluid output is (input amount * modifier))
```groovy
.setModifier(float) // 1.0 is 1:1, 2.0 is 1:2, etc
```
Set the catalyst item: (you must have a catalyst item set)
```groovy
.setCatalyst(IIngredient)
```
Register recipe: (returns a `IFermenterRecipe`)
```groovy
.register()
```
### Example
```groovy
mods.forestry.Fermenter.recipeBuilder()
    .fluidInput(fluid('lava'))
    .fluidOutput(fluid('for.honey'))
    .setCatalyst(ore('beeComb')) // any "beeComb" item
    .setAmount(150) // use 150 lava
    .setModifier(3.0F) // create 450 honey (150 * 3.0 = 450)
    .register()
```
## Removing Recipes
Remove this exact recipe:
```groovy
mods.forestry.Fermenter.remove(IFermenterRecipe)
```
Remove all recipes that use the given fluid:
```groovy
mods.forestry.Fermenter.removeByInput(FluidStack)
```
Remove all recipes that result in the given fluid:
```groovy
mods.forestry.Fermenter.removeByOutput(FluidStack)
```
Remove all recipes that use the given catalyst item:
```groovy
mods.forestry.Fermenter.removeByCatalyst(IIngredient)
```
### Example
```groovy
mods.forestry.Fermenter.removeByInput(fluid('juice')) // removes recipes that use fruit juice
mods.forestry.Fermenter.removeByOutput(fluid('short.mead')) // removes recipes that create short mead
mods.forestry.Fermenter.removeByCatalyst(item('minecraft:reeds')) // remove recipes that use sugar canes as a catalyst
```
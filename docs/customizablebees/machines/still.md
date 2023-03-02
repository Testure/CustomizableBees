# Forestry Still
## Adding Recipes
The Still uses a recipe builder much like other recipe types.
```groovy
mods.forestry.Still.recipeBuilder()
```
Adding an input fluid: (requires exactly 1)
```groovy
.fluidInput(FluidStack)
```
Adding an output fluid: (requires exactly 1)
```groovy
.fluidOutput(FluidStack)
```
Set the time this recipe takes:
```groovy
.setTime(20) // 20 is the default
```
Register recipe: (returns a `IStillRecipe`)
```groovy
.register()
```
### Example
```groovy
mods.forestry.Still.recipeBuilder()
    .fluidInput(fluid('water') * 20)
    .fluidOutput(fluid('lava') * 1)
    .setTime(150)
    .register()
```
## Removing Recipes
Remove this exact recipe:
```groovy
mods.forestry.Still.remove(IStillRecipe)
```
Remove all recipes that use the given fluid as an input:
```groovy
mods.forestry.Still.removeByInput(FluidStack)
```
Remove all recipes that result in the given fluid:
```groovy
mods.forestry.Still.removeByOutput(FluidStack)
```
Remove all recipes that use the given input fluid to create the given output fluid:
```groovy
mods.forestry.Still.removeByInputAndOutput(FluidStack input, FluidStack output)
```
### Example
```groovy
mods.forestry.Still.removeByInput(fluid('biomass')) // remove recipes that use biomass
mods.forestry.Still.removeByOutput(fluid('bio.ethanol')) // remove recipes that create ethanol
mods.forestry.Still.removeByInputAndOutput(fluid('water'), fluid('lava')) // remove recipes that use water to create lava
```
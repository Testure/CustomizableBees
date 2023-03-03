# Forestry Carpenter
## Adding Recipes
Like other recipe types, the Carpenter uses a recipe builder.
```groovy
mods.forestry.Carpenter.recipeBuilder()
```
Set the recipe patter: (requires 1-3 strings each with a length of 1-3 characters (each string is one slot downwards in the crafting grid))
```groovy
.setRecipePattern(String...)
```
Assign an Ingredient to a pattern character:
```groovy
.assignInput(String character, IIngredient item)
```
Set the fluid input: (requires 0-1)
```groovy
.fluidInput(FluidStack) // can be null to require no fluid
```
Set the box item:
```groovy
.setBoxItem(ItemStack) // can be null to require no box item
```
Set the output item: (requires exactly 1)
```groovy
.output(ItemStack)
```
Set the time the recipe takes:
```groovy
.setTime(int) // default is 20
```
Register recipe: (returns a `ICarpenterRecipe`)
```groovy
.register()
```
### Example
```groovy
mods.forestry.Carpenter.recipeBuilder()
    .setRecipePatter(
            "GCG",
            "CCC",
            "GCG"
    )
    .assignInput('G', item('minecraft:gravel'))
    .assignInput('C', ore('blockCoal'))
    .fluidInput(fluid('lava') * 2000)
    .setBoxItem(item('minecraft:emerald'))
    .output(item('minecraft:diamond') * 4)
    .setTime(60)
    .register()
```
## Removing Recipes
Remove this exact recipe:
```groovy
mods.forestry.Carpenter.remove(ICarpenterRecipe)
```
Remove all recipes that result in the given item:
```groovy
mods.forestry.Carpenter.removeByOutput(ItemStack)
```
Remove all recipes that use the given fluid:
```groovy
mods.forestry.Carpenter.removeByFluidInput(FluidStack)
```
Remove all recipes that use the give box item:
```groovy
mods.forestry.Carpenter.removeByBoxInput(IIngredient)
```
Remove all recipes that use the give box item _and_ the given fluid:
```groovy
mods.forestry.Carpenter.removeByInputs(IIngredient box, FluidStack fluid)
```
### Example
```groovy
mods.forestry.Carpenter.removeByOutput(item('forestry:soldering_iron')) // remove recipes that create a soldering iron
mods.forestry.Carpenter.removeByFluidInput(fluid('seed.oil')) // remove recipes that use seed oil
mods.forestry.Carpenter.removeByBoxInput(item('forestry:carton')) // remove recipes that use a carton as a box
mods.forestry.Carpenter.removeByInputs(item('forestry:crate'), fluid('water')) // remove recipes that use water and a crate as a box
```
# Customizable Bees Combs
Bee combs are a standardized Forestry item with multiple variants that act as a proxy products for bees.
By default, bee combs are obtained as a bee product then processed in the Centrifuge for various byproducts.

Customizable Bees offers an easy way to add bee comb types to the game.

Package:
```groovy
mods.customizablebees.Combs
```

_**Combs should be created during GroovyScript's preInit loader.**_

## The Comb Item
Before you can add comb types, you need to register a base item for them first.

When a comb item is created, all of the nonsense that goes into making it function properly is handled by Customizable Bees.
The one exception, however, is localization. **You must provide your own localization for each comb type.**

There are two ways you can go about do so:

First, you can create a `CombItem`, add properties to it, then register it.

Or, you can create your comb types beforehand, then create & register your comb immediately.

The latter is achieved with a single simple method:

`createAndRegisterCombItem()` takes an optional `CreativeTabs` that will determine what creative tab your combs will show up in and a var args of all comb types your comb will have.
this method returns a `CombItem`.
```groovy
mods.customizablebees.Combs.createAndRegisterCombItem(@Nullable CreativeTabs, ICombType...)
```

The former uses three methods:

`createCombItem()` takes an optional `CreativeTabs` argument explained above and returns a `CombItem`.
```groovy
mods.customizablebees.Combs.createCombItem(@Nullable CreativeTabs)
```

`CombItem` has a method to add a new `ICombType`.
this method should be called _**only** before_ your `CombItem` is registered.
```groovy
CombItem.addType(ICombType)
```

`addComb()` takes in a `CombItem` and registers it.
it also returns the very `CombItem` it was given.
```groovy
mods.customizablebees.Combs.addComb(CombItem)
```

Please keep in mind that you should only register one comb item at once.

## Comb Types
Comb types define a specific variant of bee comb. They are created through a simple builder.

You can obtain this builder by invoking:
`combTypeBuilder()` when given the name of your new comb type will return a `com.turing.customizablebees.api.CombTypeBuilder`.
```groovy
mods.customizablebees.Combs.typeBuilder(String)
```

### CombTypeBuilder
`CombTypeBuilder` has a few basic methods you can use to customize your comb type.

If you want your comb to be hidden from both JEI and all creative tabs, you can call `setIsSecret()`.
```groovy
CombTypeBuilder.setIsSecret()
```

The primary color of a bee comb is the color of the 'hexagons' in the comb texture.
You can change the primary color by invoking `setPrimaryColor()`.
```groovy
CombTypeBuilder.setPrimaryColor(int)
// OR
CombTypeBuilder.setPrimaryColor(int, int, int)
// OR
CombTypeBuilder.setPrimaryColor(java.awt.Color)
```

The secondary color of a bee comb is the color of the outline part in its texture.
You can change the secondary color by invoking `setSecondaryColor()`.
```groovy
CombTypeBuilder.setSecondaryColor(int)
// OR
CombTypeBuilder.setSecondaryColor(int, int, int)
// OR
CombTypeBuilder.setSecondaryColor(java.awt.Color)
```

When you're done, you need to 'build' the comb type to obtain a `ICombType` object.
```groovy
CombTypeBuilder.build()
```

Remember that you can chain the methods of a builder!

## Comb Recipes
You can define the byproducts of a particular bee comb type by creating a comb recipe.

Comb recipes are Centrifuge recipes that take the parenting type of bee comb and produce various item byproducts.
Comb recipes are also created using a builder, Which can be obtained using:
`combRecipeBuilder()` takes the `ICombType` you want to make a recipe for and returns a `CombRecipeBuilder`.
```groovy
mods.customizablebees.Combs.recipeBuilder(ICombType)
```

### CombRecipeBuilder
`CombRecipeBuilder` only has two simple methods:

`addProduct()` which adds a new `ItemStack` to be produced in the recipe, and optionally, a `float` between 0 - 1 to determine the chance of the item being produced.
```groovy
CombRecipeBuilder.addProduct(ItemStack)
// OR
CombRecipeBuilder.addProduct(ItemStack, float)
```

!!! Note
    these methods take in an `ItemStack` and NOT an `IIngredient`.

When you've defined the products you want, you can build the recipe, which will automatically register it for you.
To build a comb recipe, you must provide the base `CombItem`.
```groovy
CombRecipeBuilder.build(CombItem)
```

## Example
The following is an example of adding custom comb types into the game.

```groovy
// in a pre init script:
import com.turing.customizablebees.BeeCombEvent
import java.awt.Color

def type1 = mods.customizablebees.Combs.typeBuilder("amogus").setPrimaryColor(0xFFFFFF).setSecondaryColor(200, 200, 0).build()
def type2 = mods.customizablebees.Combs.typeBuilder("epic").setPrimaryColor(new Color(200, 200, 0)).setSecondaryColor(120, 0, 80).build()

def myComb = mods.customizablebees.Combs.createCombItem(null)

myComb.addType(type1)
myComb.addType(type2)

mods.customizablebees.Combs.addComb(myComb)

// in a post init script:
def myComb = mods.customizablebees.Combs.getComb()
mods.customizablebees.Combs.recipeBuilder(myComb.get(1))
    .addProduct(item('minecraft:apple') * 2)
    .addProduct(item('minecraft:iron_ingot') * 1, 0.2F)
    .build(myComb)
```
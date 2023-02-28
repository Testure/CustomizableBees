# Customizable Bees
Customizable Bees is a mod that exposes some Forestry compatibility methods to GroovyScript.

The examples in this documentation use `customizablebees` as a modid. This can be replaced with the following:
`custombees`, `forestry`, and `forestrytweaker`. This does not apply when importing java classes.

## Basic API
The basic API can be accessed with:
```groovy
mods.customizablebees
```

**_The Customizable Bees API is designed to run on GroovyScript's preInit loader._**

### SpeciesEntry
A SpeciesEntry is a custom interface that defines specific species of bee.

It can be located here: `com.turing.customizablebees.bees.BeeMutationTree.SpeciesEntry`

A SpeciesEntry object isn't of much use by itself, but it will be required for various API methods.

There are two methods in `SpeciesEntry` that you should worry about:

!!! Note
    Modded bee species have a name format of `${modid}.${speciesName}`,
    but Forestry species have a name format of `forestry.species${speciesName}`
    with the species name having it's first letter capitalized.

`getEntryName()` returns a string containing the name of the species
```
String getEntryName()
```

`get()` returns the Forestry-API-level species object
```
IAlleleBeeSpecies get()
```

### Mutations
Bee mutations are effectively bee recipes, they let you define your own bee breeding recipes.
Mutations are made up of 4 components with an additional optional component:
1. The first `SpeciesEntry` input
2. The second `SpeciesEntry` input
3. The `SpeciesEntry` output
4. a `double` between the range of 0 - 1 that determines the chance of the mutation happening
5. a requirement function that defines additional prerequisites before the mutation can occur

Creating a basic mutation goes as follows:
```groovy
//                                            input1        input2        output  chance
mods.customizablebees.createBeeMutation(SpeciesEntry, SpeciesEntry, SpeciesEntry, double)
```
`createBeeMutation()` also has overrides that let you use species names instead of `SpeciesEntry` objects.
```groovy
//                                      input1  input2  output  chance
mods.customizablebees.createBeeMutation(String, String, String, double)
```
Adding a special requirement is fairly simple:
```groovy
mods.customizablebees.creeateBeeMutation(SpeciesEntry, SpeciesEntry, SpeciesEntry, double, (MutationConditionBuilder builder) -> {
    return builder
})
```
The requirement function will take in a `com.turing.customizablebees.api.MutationConditionBuilder` object and returns a modified version of that same object.
Exact details on how the `MutationConditionBuilder` can be used can be found in Mutations doc page.

You can use strings instead of `SpeciesEntry` objects while also providing a requirement function.

### Products & Specialties
Products are the direct `ItemStack` outputs that a bee makes while working.
Specialties are products that are only produced when a bee's unique jubilance conditions are met.

You can edit bee products and specialties with, but with two key limitations:
1. Products and specialties can be added, but not removed
2. Products and specialties can only be added to bee types added by Customizable Bees

To add a product to a species:
```groovy
mods.customizablebees.addProduct(SpeciesEntry, ItemStack, float)
```
Specialties are the same:
```groovy
mods.customizablebees.addSpecialty(SpeciesEntry, ItemStack, float)
```
The provided `float` should be a number between the range of 0 - 1, it is the chance of the item being produced.

These methods have overrides that let you use a species name instead of a `SpeciesEntry` object.

!!! Note 
    these methods take in an `ItemStack` and NOT an `IIngredient`.

### Obtaining a bee species
There are two methods for obtaining a `SpeciesEntry` object.

The standard method:
`getBeeSpecies()` takes in a species name (see naming format above) and returns a `SpeciesEntry` of that species.
A NullPointerException might be produced if the species does not exist.
```groovy
mods.customizablebees.getBeeSpecies(String)
```
The Forestry method:
`getForestryBeeSpecies()` is the same as `getBeeSpecies()` but will automatically append the required name format on the given species name.
This means that instead of getting a Common bee using the string `'forestry.speciesCommon'` you can simply use the string `'common'`.
```groovy
mods.customizablebees.getForestryBeeSpecies(String)
```

### Effects
Effects are custom behaviors that particular species of bee will have when placed in proper housing.
Effects typically focus on interaction with the world and nearby blocks/entities, but can be made to have other behaviors as well.

You can create a custom effect by creating a class that extends `com.turing.customizablebees.bees.effects.EffectBase` or any other class that extends it.
The specifics of creating a custom event will be explained in its own doc page.

Once you've created your custom effect, it is not recommended to create an instance of it.
Instead, you should register it with this method:
`addEffect()` takes in a name string and a reference to your effect class and automatically registers your effect.
This name can then be used to give this effect to one of your custom bees.
```groovy
mods.cutomizablebees.addEffect(String, Class<EffectBase>)
```
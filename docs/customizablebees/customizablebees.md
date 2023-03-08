# Customizable Bees
Customizable Bees is a mod that exposes some Forestry compatibility methods to GroovyScript.

The examples in this documentation use `customizablebees` as a modid. This can be replaced with the following:
`custombees`, `forestry`, and `forestrytweaker`. This does not apply when importing java classes.

## Basic API
The basic API can be accessed with:
```groovy
mods.customizablebees
```

### Obtaining a bee species
A bee species can be obtained using the `species` bracket handler.
It returns an `AlleleBeeSpecies` object.
```groovy
species("forestry:common")
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
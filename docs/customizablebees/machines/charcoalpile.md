# Forestry Charcoal Pile
Customizable Bees lets you add or remove wall blocks for the charcoal pile multiblock.

## Adding walls
```groovy
mods.forestry.CharcoalPile.addWall(Block block, int amount)
```
```groovy
mods.forestry.CharcoalPile.addWallState(IBlockState state, int amount)
```
```groovy
mods.forestry.CharcoalPile.addWallStack(ItemStack blockItem, int amount)
```
### Example
```groovy
mods.forestry.CharcoalPile.addWall(blockstate('minecraft:iron_block').getBlock(), 2)
```
```groovy
mods.forestry.CharcoalPile.addWallState(blockstate('minecraft:obsidian'), 5)
```
```groovy
mods.forestry.CharcoalPile.addWallStack(item('minecraft:stone'), 1)
```
## Removing walls
```groovy
mods.forestry.CharcoalPile.removeWall(Block block)
```
```groovy
mods.forestry.CharcoalPile.removeWallState(IBlockState state)
```
```groovy
mods.forestry.CharcoalPile.removeWallStack(ItemStack blockItem)
```
### Example
```groovy
mods.forestry.CharcoalPile.removeWall(blockstate('minecraft:dirt').getBlock())
```
```groovy
mods.forestry.CharcoalPile.removeWallState(blockstate('minecraft:gravel'))
```
```groovy
mods.forestry.CharcoalPile.removeWallStack(item('minecraft:clay'))
```

You could also simply remove all walls.
```groovy
mods.forestry.CharcoalPile.removeAllWalls()
```
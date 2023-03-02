package com.turing.customizablebees.api.groovyscript;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import forestry.api.arboriculture.ICharcoalManager;
import forestry.api.arboriculture.ICharcoalPileWall;
import forestry.api.arboriculture.TreeManager;
import forestry.arboriculture.charcoal.CharcoalPileWall;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class CharcoalPile extends VirtualizedRegistry<ICharcoalPileWall> {
    protected static ICharcoalManager manager;

    public CharcoalPile() {
        super();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        manager = TreeManager.charcoalManager;
        if (manager == null) return;
        removeScripted().forEach(manager.getWalls()::remove);
        restoreFromBackup().forEach(manager::registerWall);
    }

    public ICharcoalPileWall addWallState(IBlockState state, int amount) {
        manager = TreeManager.charcoalManager;
        if (manager == null || state == null) return null;
        CharcoalPileWall wall = new CharcoalPileWall(state, amount);
        addScripted(wall);
        manager.registerWall(wall);
        return wall;
    }

    public ICharcoalPileWall addWall(Block block, int amount) {
        manager = TreeManager.charcoalManager;
        if (manager == null || block == null) return null;
        CharcoalPileWall wall = new CharcoalPileWall(block, amount);
        addScripted(wall);
        manager.registerWall(wall);
        return wall;
    }

    public ICharcoalPileWall addWallStack(ItemStack stack, int amount) {
        manager = TreeManager.charcoalManager;
        if (manager == null || stack == null || stack.isEmpty()) return null;
        if (!(stack.getItem() instanceof ItemBlock)) return null;
        return addWall(((ItemBlock) stack.getItem()).getBlock(), amount);
    }

    public boolean remove(ICharcoalPileWall wall) {
        manager = TreeManager.charcoalManager;
        if (manager == null || wall == null) return false;
        addBackup(wall);
        manager.getWalls().remove(wall);
        return true;
    }

    public boolean removeWall(Block block) {
        manager = TreeManager.charcoalManager;
        if (manager == null || block == null) return false;
        if (manager.getWalls().removeIf(wall -> {
            boolean found = wall.matches(block.getDefaultState());
            if (found) addBackup(wall);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry CharcoalPile wall")
                .add("Could not find wall with block %s", block)
                .error()
                .post();
        return false;
    }

    public boolean removeWallState(IBlockState state) {
        manager = TreeManager.charcoalManager;
        if (manager == null || state == null) return false;
        if (manager.getWalls().removeIf(wall -> {
            boolean found = wall.matches(state);
            if (found) addBackup(wall);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry CharcoalPile wall")
                .add("Could not find wall with BlockState %s", state)
                .error()
                .post();
        return false;
    }

    public boolean removeWallStack(ItemStack stack) {
        manager = TreeManager.charcoalManager;
        if (manager == null || stack == null || stack.isEmpty()) return false;
        if (!(stack.getItem() instanceof ItemBlock)) return false;
        return removeWall(((ItemBlock) stack.getItem()).getBlock());
    }

    public void removeAllWalls() {
        manager = TreeManager.charcoalManager;
        if (manager == null) return;
        manager.getWalls().forEach(this::addBackup);
        manager.getWalls().clear();
    }

    public SimpleObjectStream<ICharcoalPileWall> streamWalls() {
        manager = TreeManager.charcoalManager;
        if (manager == null) return null;
        return new SimpleObjectStream<>(manager.getWalls()).setRemover(this::remove);
    }
}

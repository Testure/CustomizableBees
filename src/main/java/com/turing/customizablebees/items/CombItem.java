package com.turing.customizablebees.items;

import com.google.common.collect.ImmutableList;
import com.turing.customizablebees.api.ICombType;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CombItem extends BaseItem {
    public static final Map<CombItem, List<ICombType>> ALL_COMB_TYPES = new HashMap<>();

    private final List<ICombType> types = new ArrayList<>();

    public CombItem(String modid, @Nullable CreativeTabs tab) {
        super(modid, "bee_comb", tab);
        this.setHasSubtypes(true);
    }

    public void onRegister() {
        ALL_COMB_TYPES.put(this, getTypes());
    }

    public ItemStack getStackFromType(ICombType type) {
        return getStackFromType(type, 1);
    }

    public ItemStack getStackFromType(ICombType type, int amount) {
        return new ItemStack(this, amount, type.getUID());
    }

    @Nullable
    public ICombType get(int i) {
        return i >= this.types.size() ? null : this.types.get(i);
    }

    public List<ICombType> getTypes() {
        return ImmutableList.copyOf(this.types);
    }

    public ICombType addType(ICombType type) {
        int i = this.types.size();
        if (this.types.contains(type)) throw new IllegalArgumentException("Comb type '" + type.getCombName() + "' already exists for modid '" + this.modid + "'");
        this.types.add(i, type);
        type.setUID(i);
        return type;
    }

    public int getColor(ItemStack stack, int tintIndex) {
        int i = stack.getItemDamage();
        ICombType type = get(i);
        if (type != null) return tintIndex == 0 ? type.getPrimaryColor() : type.getSecondaryColor();
        else return -1;
    }

    @SideOnly(Side.CLIENT)
    public IItemColor getItemColor() {
        return this::getColor;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        ICombType type = this.types.get(stack.getItemDamage());
        return type == null ? super.getTranslationKey(stack) : getTranslationKey() + "." + type.getCombName().toLowerCase();
    }

    @Override
    public int getDamage(ItemStack stack) {
        if (this.types.size() <= super.getDamage(stack)) stack.setItemDamage(0);
        return super.getDamage(stack);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab) || (this.getCreativeTab() == null && tab == CreativeTabs.SEARCH))
            for (ICombType type : this.types)
                if (type.shouldShow())
                    items.add(this.getStackFromType(type));
    }
}

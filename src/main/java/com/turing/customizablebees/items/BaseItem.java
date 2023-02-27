package com.turing.customizablebees.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import javax.annotation.Nullable;

public class BaseItem extends Item {
    public final String modid;

    public BaseItem(String modid, String registryName, @Nullable CreativeTabs tab) {
        super();
        this.modid = modid;
        this.setTranslationKey(modid + "." + registryName);
        this.setRegistryName(modid, registryName);
        if (tab != null) this.setCreativeTab(tab);
    }
}

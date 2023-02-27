package com.turing.customizablebees.bees.effects.settings;

import com.google.common.collect.ImmutableSet;
import com.turing.customizablebees.bees.effects.EffectBase;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Filter {
    public static final Predicate<ItemStack> DEFAULT_MATCHER = stack -> true;

    final Setting.Choice<FilterType> filterType;
    final Setting.Stack stack;
    final Setting.YesNo ignoreMeta;
    final Setting.YesNo ignoreNBT;
    final Setting.OreDictText oreDictText;

    public Filter(EffectBase parent) {
        this(parent, "filter");
    }

    public Filter(EffectBase parent, String name) {
        filterType = new Setting.Choice<>(parent, name + "Type", FilterType.ITEMSTACK);

        stack = new Setting.Stack(parent, name + "Itemstack") {
            @Override
            public boolean shouldBeVisible(IEffectSettingsHolder settingsHolder) {
                return filterType.getValue(settingsHolder) == FilterType.ITEMSTACK;
            }

            @Override
            public ItemStack overrideInput(ItemStack input) {
                if (input.getCount() > 1) return ItemHandlerHelper.copyStackWithSize(input, 1);
                return input;
            }
        };

        ignoreMeta = new Setting.YesNo(parent, name + "Meta", false) {
            @Override
            public boolean shouldBeVisible(IEffectSettingsHolder settingsHolder) {
                return filterType.getValue(settingsHolder) == FilterType.ITEMSTACK;
            }
        };

        ignoreNBT = new Setting.YesNo(parent, name + "NBT", true) {
            @Override
            public boolean shouldBeVisible(IEffectSettingsHolder settingsHolder) {
                return filterType.getValue(settingsHolder) == FilterType.ITEMSTACK;
            }
        };

        oreDictText = new Setting.OreDictText(parent, name + "OreDict") {
            @Override
            public boolean shouldBeVisible(IEffectSettingsHolder settingsHolder) {
                return filterType.getValue(settingsHolder) == FilterType.OREDICTIONARY;
            }
        };
    }

    public boolean matches(IBeeHousing housing, ItemStack stack) {
        for (IBeeModifier modifier : housing.getBeeModifiers())
            if (modifier instanceof IEffectSettingsHolder) return matches((IEffectSettingsHolder) modifier, stack);
        return matches(IEffectSettingsHolder.INSTANCE, stack);
    }

    public boolean matches(IEffectSettingsHolder settingsHolder, ItemStack stack) {
        if (stack.isEmpty()) return false;
        FilterType value = filterType.getValue(settingsHolder);

        switch (value) {
            case ITEMSTACK: {
                ItemStack target = this.stack.getValue(settingsHolder);
                return target.getItem() == stack.getItem()
                        && (ignoreMeta.getValue(settingsHolder) || target.getMetadata() == stack.getMetadata())
                        && (ignoreNBT.getValue(settingsHolder) || Objects.equals(target.getTagCompound(), stack.getTagCompound()));
            }
            case OREDICTIONARY: {
                for (ItemStack itemStack : OreDictionary.getOres(oreDictText.getValue(settingsHolder), false))
                    if (OreDictionary.itemMatches(itemStack, stack, false))
                        return false;
            }
        }

        return true;
    }

    public Predicate<ItemStack> getMatcher(IEffectSettingsHolder settingsHolder) {
        if (settingsHolder == IEffectSettingsHolder.INSTANCE) return DEFAULT_MATCHER;

        FilterType value = filterType.getValue(settingsHolder);

        switch (value) {
            case ITEMSTACK: {
                ItemStack target = this.stack.getValue(settingsHolder);
                return  createSingleStackPredicate(target, ignoreMeta.getValue(settingsHolder), ignoreNBT.getValue(settingsHolder));
            }
            case OREDICTIONARY: {
                NonNullList<ItemStack> ores = OreDictionary.getOres(oreDictText.getValue(settingsHolder), false);
                if (ores.isEmpty()) return stack1 -> false;
                else if (ores.size() == 1) {
                    ItemStack target = ores.get(0);
                    return createSingleStackPredicate(target, target.getMetadata() == OreDictionary.WILDCARD_VALUE, true);
                } else {
                    ImmutableSet<Item> basicItems = ores.stream()
                            .filter(stack1 -> stack1.getMetadata() == OreDictionary.WILDCARD_VALUE)
                            .map(ItemStack::getItem)
                            .collect(ImmutableSet.toImmutableSet());
                    Map<Item, ImmutableSet<Integer>> advItems = ores.stream()
                            .filter(stack1 -> stack1.getMetadata() == OreDictionary.WILDCARD_VALUE)
                            .collect(Collectors.groupingBy(
                                    ItemStack::getItem,
                                    Collectors.mapping(ItemStack::getMetadata, ImmutableSet.toImmutableSet())
                            ));
                    Predicate<ItemStack> basicPredicate = basicItems.isEmpty() ? null : stack1 -> basicItems.contains(stack1.getItem());
                    Predicate<ItemStack> advPredicate = advItems.isEmpty() ? null : stack1 -> advItems.getOrDefault(stack1.getItem(), ImmutableSet.of()).contains(stack1.getItemDamage());

                    if (basicPredicate == null) return advPredicate == null ? (stack1 -> false) : advPredicate;
                    if (advPredicate == null) return basicPredicate;

                    return basicPredicate.and(advPredicate);
                }
            }
        }

        return stack1 -> false;
    }

    public Predicate<ItemStack> createSingleStackPredicate(ItemStack target, boolean ignoreMetaData, boolean ignoreNBT) {
        if (target.isEmpty()) return s -> true;
        Item item = target.getItem();
        Predicate<ItemStack> predicate = stack1 -> stack1.getItem() == item;
        if (!ignoreMetaData) predicate = predicate.and(stack1 -> stack1.getMetadata() == target.getMetadata());
        if (!ignoreNBT) predicate = predicate.and(stack1 -> Objects.equals(stack1.getTagCompound(), target.getTagCompound()));

        return predicate;
    }

    public enum FilterType {
        ITEMSTACK,
        OREDICTIONARY;
    }
}

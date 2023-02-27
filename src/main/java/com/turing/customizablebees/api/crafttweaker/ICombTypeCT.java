package com.turing.customizablebees.api.crafttweaker;

import com.turing.customizablebees.api.ICombType;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.customizablebees.ICombType")
public interface ICombTypeCT extends ICombType {
    @Override
    @ZenMethod
    int getPrimaryColor();

    @Override
    @ZenMethod
    int getSecondaryColor();

    @Override
    @ZenMethod
    String getCombName();

    @Override
    @ZenMethod
    default boolean shouldShow() {
        return ICombType.super.shouldShow();
    }
}

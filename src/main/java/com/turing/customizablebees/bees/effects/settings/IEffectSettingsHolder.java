package com.turing.customizablebees.bees.effects.settings;

public interface IEffectSettingsHolder {
    <V> V getValue(Setting<V, ?> setting);

    IEffectSettingsHolder INSTANCE = Setting::getDefault;
}

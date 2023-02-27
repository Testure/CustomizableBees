package com.turing.customizablebees.api;

public interface ICombType {
    int getPrimaryColor();

    int getSecondaryColor();

    int getUID();

    void setUID(int UID);

    default boolean shouldShow() {
        return true;
    }

    String getCombName();
}

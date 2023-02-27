package com.turing.customizablebees.api;

import java.awt.*;

public class CombTypeBuilder {
    private final String name;
    private boolean shouldShow = true;
    private int primaryColor;
    private int secondaryColor;

    protected CombTypeBuilder(String name) {
        this.name = name;
    }

    public static CombTypeBuilder start(String name) {
        return new CombTypeBuilder(name);
    }

    public CombTypeBuilder setIsSecret() {
        this.shouldShow = false;
        return this;
    }

    public CombTypeBuilder setPrimaryColor(int color) {
        this.primaryColor = color;
        return this;
    }

    public CombTypeBuilder setSecondaryColor(int color) {
        this.secondaryColor = color;
        return this;
    }

    public CombTypeBuilder setPrimaryColor(int r, int g, int b) {
        this.primaryColor = new Color(r, g, b).getRGB();
        return this;
    }

    public CombTypeBuilder setSecondaryColor(Color color) {
        this.primaryColor = color.getRGB();
        return this;
    }

    public CombTypeBuilder setPrimaryColor(Color color) {
        this.primaryColor = color.getRGB();
        return this;
    }

    public CombTypeBuilder setSecondaryColor(int r, int g, int b) {
        this.secondaryColor = new Color(r, g, b).getRGB();
        return this;
    }

    public ICombType build() {
        return new ICombType() {
            private int UID;

            @Override
            public int getPrimaryColor() {
                return primaryColor;
            }

            @Override
            public int getSecondaryColor() {
                return secondaryColor;
            }

            @Override
            public int getUID() {
                return UID;
            }

            @Override
            public void setUID(int UID) {
                this.UID = UID;
            }

            @Override
            public String getCombName() {
                return name;
            }

            @Override
            public boolean shouldShow() {
                return shouldShow;
            }
        };
    }
}

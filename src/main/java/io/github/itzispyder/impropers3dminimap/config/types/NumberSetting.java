package io.github.itzispyder.impropers3dminimap.config.types;

import io.github.itzispyder.impropers3dminimap.config.Setting;

public abstract class NumberSetting<T extends Number> extends Setting<T> {

    protected T min, max;

    public NumberSetting(String name, T def, T val, T min, T max) {
        super(name, def, val);
        this.min = min;
        this.max = max;
    }

    public T getMin() {
        return min;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMax() {
        return max;
    }

    public void setMax(T max) {
        this.max = max;
    }
}

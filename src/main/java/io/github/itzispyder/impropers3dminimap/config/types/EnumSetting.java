package io.github.itzispyder.impropers3dminimap.config.types;

import io.github.itzispyder.impropers3dminimap.config.Setting;
import io.github.itzispyder.impropers3dminimap.config.SettingBuilder;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.config.settings.types.EnumSettingElement;

public class EnumSetting<T extends Enum<?>> extends Setting<T> {

    private final T[] array;

    @SuppressWarnings("unchecked")
    public EnumSetting(String name, T val) {
        super(name, val);
        this.array = (T[])val.getClass().getEnumConstants();
    }

    public T[] getArray() {
        return array;
    }

    public T valueOf(String name) {
        for (T t : array)
            if (t.name().equals(name))
                return t;
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public EnumSettingElement<T> toGuiElement(int x, int y) {
        return new EnumSettingElement<>(this, x, y, 200 - 20);
    }

    public static <T extends Enum<?>> Builder<T> create(Class<T> type) {
        return new Builder<>();
    }

    public static class Builder<T extends Enum<?>> extends SettingBuilder<T, Builder<T>, EnumSetting<T>> {
        @Override
        protected EnumSetting<T> buildSetting() {
            return new EnumSetting<>(name, def);
        }
    }
}

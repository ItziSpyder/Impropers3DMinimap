package io.github.itzispyder.impropers3dminimap.config.types;

import io.github.itzispyder.impropers3dminimap.config.SettingBuilder;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.config.settings.types.IntegerSettingElement;
import io.github.itzispyder.impropers3dminimap.util.math.MathUtils;

public class IntegerSetting extends NumberSetting<Integer> {

    public IntegerSetting(String name, int def, int val, int min, int max) {
        super(name, def, val, min, max);
    }

    @Override
    @SuppressWarnings("unchecked")
    public IntegerSettingElement toGuiElement(int x, int y) {
        return new IntegerSettingElement(this, x, y, 200 - 20);
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends SettingBuilder<Integer, Builder, IntegerSetting> {

        private int min, max;

        public Builder() {
            this.min = 0;
            this.max = 1;
        }

        public Builder min(int min) {
            this.min = Math.min(min, max);
            return this;
        }

        public Builder max(int max) {
            this.max = Math.max(min, max);
            return this;
        }

        @Override
        public IntegerSetting buildSetting() {
            return new IntegerSetting(name, MathUtils.clamp(def, min, max), getOrDef(val, def), min, max);
        }
    }
}

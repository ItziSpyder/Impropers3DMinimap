package io.github.itzispyder.impropers3dminimap.config.types;

import io.github.itzispyder.impropers3dminimap.config.Setting;
import io.github.itzispyder.impropers3dminimap.config.SettingBuilder;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.config.settings.types.BooleanSettingElement;

public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(String name, boolean def, boolean val) {
        super(name, def, val);
    }

    @Override
    @SuppressWarnings("unchecked")
    public BooleanSettingElement toGuiElement(int x, int y) {
        return new BooleanSettingElement(this, x, y, 200 - 20);
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends SettingBuilder<Boolean, Builder, BooleanSetting> {
        @Override
        public BooleanSetting buildSetting() {
            return new BooleanSetting(name, def, getOrDef(val, def));
        }
    }
}

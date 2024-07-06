package io.github.itzispyder.impropers3dminimap.render.ui.elements.config.settings;

import io.github.itzispyder.impropers3dminimap.config.Setting;
import io.github.itzispyder.impropers3dminimap.render.ui.GuiElement;

public abstract class SettingElement<T extends Setting<?>> extends GuiElement {

    public final T setting;

    public SettingElement(T setting, int x, int y, int width) {
        super(x, y, width, 10);
        this.setting = setting;
    }

    public T getSetting() {
        return setting;
    }
}

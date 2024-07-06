package io.github.itzispyder.impropers3dminimap.render.ui.hud;

import io.github.itzispyder.impropers3dminimap.config.Setting;

public interface SettingDependent<T> {

    Setting<T> provideSetting();

    boolean checkSetting(Setting<T> setting);

    default boolean passesCheck() {
        return checkSetting(provideSetting());
    }
}

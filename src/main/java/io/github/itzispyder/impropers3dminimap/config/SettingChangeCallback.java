package io.github.itzispyder.impropers3dminimap.config;

@FunctionalInterface
public interface SettingChangeCallback<T extends Setting<?>> {

    void onChange(T setting);
}

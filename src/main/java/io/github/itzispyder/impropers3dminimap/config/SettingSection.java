package io.github.itzispyder.impropers3dminimap.config;

import io.github.itzispyder.impropers3dminimap.util.misc.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SettingSection implements SettingContainer {

    private final String name, id;
    private final List<Setting<?>> settings;

    public SettingSection(String name) {
        this.id = name;
        this.name = StringUtils.capitalizeWords(name);
        this.settings = new ArrayList<>();
    }

    public <T, S extends Setting<T>> S add(S setting) {
        this.settings.add(setting);
        return setting;
    }

    public void remove(Setting<?> setting) {
        this.settings.remove(setting);
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }

    @Override
    public List<SettingSection> getContents() {
        return List.of(this);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void forEach(Consumer<Setting<?>> action) {
        this.settings.forEach(action);
    }
}

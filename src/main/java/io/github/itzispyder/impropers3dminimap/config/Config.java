package io.github.itzispyder.impropers3dminimap.config;

import io.github.itzispyder.impropers3dminimap.util.misc.JsonSerializable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Config implements SettingContainer, JsonSerializable<Config> {

    public static final String PATH = "config/impropers3DMinimap";

    private final List<SettingSection> settings;

    public Config() {
        this.settings = new ArrayList<>();
    }

    public SettingSection createSettingSection(String id) {
        SettingSection section = new SettingSection(id);
        settings.add(section);
        return section;
    }

    @Override
    public List<SettingSection> getContents() {
        return settings;
    }

    @Override
    public File getFile() {
        return new File(PATH);
    }
}

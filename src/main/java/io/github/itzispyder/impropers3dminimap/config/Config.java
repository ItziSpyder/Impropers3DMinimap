package io.github.itzispyder.impropers3dminimap.config;

import io.github.itzispyder.impropers3dminimap.render.ui.Positionable;
import io.github.itzispyder.impropers3dminimap.render.ui.hud.Hud;
import io.github.itzispyder.impropers3dminimap.util.misc.JsonSerializable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config implements SettingContainer, JsonSerializable<Config> {

    public static final String PATH = "config/impropers3DMinimap.json";

    private transient final List<SettingSection> settings;

    private final SettingData data;
    private final Map<String, Positionable.Dimension> huds;

    public Config() {
        this.settings = new ArrayList<>();
        this.data = new SettingData("settings", this);
        this.huds = new HashMap<>();
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

    @Override
    public void save() {
        for (SettingSection section : settings)
            for (Setting<?> setting : section.getSettings())
                data.add(setting);

        for (Hud hud : Hud.huds().values())
            huds.put(hud.getId(), hud.getDimensions());

        JsonSerializable.super.save();
    }

    public void load() {
        for (SettingSection section : settings)
            for (Setting<?> setting : section.getSettings())
                data.revert(setting);

        for (Hud hud : Hud.huds().values()) {
            Positionable.Dimension pos = huds.get(hud.getId());
            if (pos == null)
                continue;
            hud.setX(pos.getX());
            hud.setY(pos.getY());
        }
    }
}

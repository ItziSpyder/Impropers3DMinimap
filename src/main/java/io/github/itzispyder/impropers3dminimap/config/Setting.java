package io.github.itzispyder.impropers3dminimap.config;

import io.github.itzispyder.impropers3dminimap.render.ui.GuiElement;
import io.github.itzispyder.impropers3dminimap.util.misc.StringUtils;

public abstract class Setting<T> {

    private SettingChangeCallback<Setting<T>> changeAction;
    private final String name, id;
    protected T def, val;

    protected Setting(String name, T def, T val) {
        this.id = name;
        this.name = StringUtils.capitalizeWords(name);
        this.def = def;
        this.val = val;
        this.changeAction = setting -> {};
    }

    protected Setting(String name, T val) {
        this(name, val, val);
    }

    public abstract <E extends GuiElement> E toGuiElement(int x, int y);

    public Class<T> getType() {
        return (Class<T>)val.getClass();
    }

    public String getName() {
        return name;
    }

    public T getDef() {
        return def;
    }

    public void setDef(T def) {
        this.def = def;
    }

    public T getVal() {
        return val;
    }

    public void setVal(Object val) {
        this.val = (T)val;
        this.changeAction.onChange(this);
    }

    public String getId() {
        return id;
    }

    public SettingChangeCallback<Setting<T>> getChangeAction() {
        return changeAction;
    }

    public void setChangeAction(SettingChangeCallback<Setting<T>> changeAction) {
        this.changeAction = changeAction;
    }

    public class Builder extends SettingBuilder<T, Builder, Setting<T>> {
        @Override
        public Setting<T> buildSetting() {
            return null;
        }
    }
}

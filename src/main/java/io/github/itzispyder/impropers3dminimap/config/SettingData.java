package io.github.itzispyder.impropers3dminimap.config;

import io.github.itzispyder.impropers3dminimap.config.types.*;
import io.github.itzispyder.impropers3dminimap.util.misc.Dictionary;

import java.util.HashMap;
import java.util.Map;

public class SettingData {

    private final String id;
    private Map<String, Object> objectEntries;
    private Map<String, Integer> integerEntries;
    private Map<String, Double> doubleEntries;
    private Map<String, Boolean> booleanEntries;
    private Map<String, String> stringEntries;
    private Map<String, Map<String, Boolean>> dictionaryEntries;

    public SettingData(String id, SettingContainer module) {
        this.id = id;
        this.objectEntries = new HashMap<>();
        this.integerEntries = new HashMap<>();
        this.doubleEntries = new HashMap<>();
        this.booleanEntries = new HashMap<>();
        this.dictionaryEntries = new HashMap<>();
        this.stringEntries = new HashMap<>();

        module.getContents().forEach(this::add);
    }

    public void nullFixIfNeed() {
        if (objectEntries == null)
            objectEntries = new HashMap<>();
        if (integerEntries == null)
            integerEntries = new HashMap<>();
        if (doubleEntries == null)
            doubleEntries = new HashMap<>();
        if (booleanEntries == null)
            booleanEntries = new HashMap<>();
        if (dictionaryEntries == null)
            dictionaryEntries = new HashMap<>();
        if (stringEntries == null)
            stringEntries = new HashMap<>();
    }

    public <T> void add(Setting<T> setting) {
        nullFixIfNeed();

        T d = setting.getVal();
        String id = setting.getId();

        if (d == null)
            setting.setVal(setting.getDef());

        if (setting instanceof EnumSetting<?> v) {
            stringEntries.put(id, v.getVal().name());
        }
        else if (d instanceof Dictionary<?> v) {
            dictionaryEntries.put(id, v.getDictionary());
        }
        else if (d instanceof Integer v) {
            integerEntries.put(id, v);
        }
        else if (d instanceof Double v) {
            doubleEntries.put(id, v);
        }
        else if (d instanceof Boolean v) {
            booleanEntries.put(id, v);
        }
        else {
            objectEntries.put(id, d);
        }
    }

    public <T> void revert(Setting<T> setting) {
        nullFixIfNeed();

        String id = setting.getId();

        if (setting instanceof EnumSetting<?> v) {
            v.setVal(v.valueOf(stringEntries.getOrDefault(id, v.getDef().name())));
        }
        else if (setting instanceof DictionarySetting<?> v) {
            v.getVal().overwrite(dictionaryEntries.getOrDefault(id, new HashMap<>()), true);
        }
        else if (setting instanceof IntegerSetting v) {
            v.setVal(integerEntries.getOrDefault(id, v.getDef()));
        }
        else if (setting instanceof DoubleSetting v) {
            v.setVal(doubleEntries.getOrDefault(id, v.getDef()));
        }
        else if (setting instanceof BooleanSetting v) {
            v.setVal(booleanEntries.getOrDefault(id, v.getDef()));
        }
        else {
            setting.setVal(objectEntries.getOrDefault(id, setting.getDef()));
        }
    }

    public void add(SettingSection section) {
        section.forEach(this::add);
    }

    public Map<String, Object> getObjectEntries() {
        return objectEntries;
    }

    public Map<String, Integer> getIntegerEntries() {
        return integerEntries;
    }

    public Map<String, Double> getDoubleEntries() {
        return doubleEntries;
    }

    public Map<String, Boolean> getBooleanEntries() {
        return booleanEntries;
    }

    public Map<String, Map<String, Boolean>> getDictionaryEntries() {
        return dictionaryEntries;
    }

    public Map<String, String> getStringEntries() {
        return stringEntries;
    }

    public Map<String, Object> getAllEntries() {
        return new HashMap<>() {{
            this.putAll(objectEntries);
            this.putAll(integerEntries);
            this.putAll(doubleEntries);
            this.putAll(booleanEntries);
            this.putAll(stringEntries);
            this.putAll(dictionaryEntries);
        }};
    }

    public String getId() {
        return id;
    }

    private <T> T getOrDef(T val, T def) {
        return val != null ? val : def;
    }
}

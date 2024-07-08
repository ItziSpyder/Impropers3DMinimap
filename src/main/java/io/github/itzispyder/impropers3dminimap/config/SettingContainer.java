package io.github.itzispyder.impropers3dminimap.config;

import io.github.itzispyder.impropers3dminimap.config.types.*;

import java.util.List;

public interface SettingContainer {

    default IntegerSetting.Builder createIntSetting() {
        return IntegerSetting.create();
    }

    default DoubleSetting.Builder createDoubleSetting() {
        return DoubleSetting.create();
    }

    default BooleanSetting.Builder createBoolSetting() {
        return BooleanSetting.create();
    }

    default EntitiesSetting.Builder createEntitiesSetting() {
        return EntitiesSetting.create();
    }

    default BlocksSetting.Builder createBlocksSetting() {
        return BlocksSetting.create();
    }

    default <T> DictionarySetting.Builder<T> createDictionarySetting(Class<T> keyType) {
        return DictionarySetting.create(keyType);
    }

    default <T extends Enum<?>> EnumSetting.Builder<T> createEnumSetting(Class<T> type) {
        return EnumSetting.create(type);
    }

    List<SettingSection> getContents();
}
